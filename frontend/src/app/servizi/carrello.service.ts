import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Carrello, RigaCarrello } from '../modelli/carrello';

@Injectable({ providedIn: 'root' })
export class CarrelloService {
  private readonly urlBase = 'http://localhost:8080/api/carrello';

  readonly numeroArticoli = signal(0);

  constructor(private readonly http: HttpClient) {}

  private conta(righe: { quantita: number }[] | null | undefined): number {
    return (righe ?? []).reduce((somma, r) => somma + r.quantita, 0);
  }

  getDettagli(): Observable<RigaCarrello[]> {
    return this.http.get<RigaCarrello[]>(this.urlBase).pipe(
      tap((righe) => this.numeroArticoli.set(this.conta(righe)))
    );
  }

  aggiungi(idProdotto: number, quantita: number): Observable<Carrello> {
    return this.http.post<Carrello>(`${this.urlBase}/aggiungi`, { idProdotto, quantita }).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello?.dettagli)))
    );
  }

  aggiornaQuantita(idProdotto: number, nuovaQuantita: number): Observable<Carrello> {
    return this.http.put<Carrello>(`${this.urlBase}/quantita`, { idProdotto, quantita: nuovaQuantita }).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello?.dettagli)))
    );
  }

  rimuovi(idProdotto: number): Observable<Carrello> {
    return this.http.delete<Carrello>(`${this.urlBase}/prodotto/${idProdotto}`).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello?.dettagli)))
    );
  }

  svuota(): Observable<Carrello> {
    return this.http.delete<Carrello>(this.urlBase).pipe(
      tap((carrello) => this.numeroArticoli.set(this.conta(carrello?.dettagli)))
    );
  }

  azzeraConteggio(): void {
    this.numeroArticoli.set(0);
  }
}
