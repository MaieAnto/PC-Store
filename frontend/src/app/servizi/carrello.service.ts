import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Carrello } from '../models/carrello';

@Injectable({ providedIn: 'root' })
export class CarrelloService {
  private readonly urlBase = 'http://localhost:8080/api/carrello';

  readonly numeroArticoli = signal(0);

  constructor(private readonly http: HttpClient) {}

  // Conta gli articoli totali nel carrello (somma delle quantita' di ogni riga).
  private conta(carrello: Carrello | null | undefined): number {
    return (carrello?.righe ?? []).reduce((somma, r) => somma + r.quantita, 0);
  }

  // Carica il carrello dell'utente e aggiorna il contatore degli articoli.
  getDettagli(): Observable<Carrello> {
    return this.http.get<Carrello>(this.urlBase).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello)))
    );
  }

  // Aggiunge un prodotto al carrello e aggiorna il contatore.
  aggiungi(idProdotto: number, quantita: number): Observable<Carrello> {
    return this.http.post<Carrello>(`${this.urlBase}/aggiungi`, { idProdotto, quantita }).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello)))
    );
  }

  // Imposta una nuova quantita' per un prodotto nel carrello.
  aggiornaQuantita(idProdotto: number, nuovaQuantita: number): Observable<Carrello> {
    return this.http.put<Carrello>(`${this.urlBase}/quantita`, { idProdotto, quantita: nuovaQuantita }).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello)))
    );
  }

  // Rimuove un prodotto dal carrello.
  rimuovi(idProdotto: number): Observable<Carrello> {
    return this.http.delete<Carrello>(`${this.urlBase}/prodotto/${idProdotto}`).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello)))
    );
  }

  // Svuota completamente il carrello.
  svuota(): Observable<Carrello> {
    return this.http.delete<Carrello>(this.urlBase).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello)))
    );
  }

  // Azzera il contatore degli articoli (es. al logout).
  azzeraConteggio(): void {
    this.numeroArticoli.set(0);
  }
}
