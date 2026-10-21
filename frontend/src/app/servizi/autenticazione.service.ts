import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { DatiRegistrazione, RispostaLogin, Utente } from '../modelli/utente';

@Injectable({ providedIn: 'root' })
export class AutenticazioneService {
  private readonly urlBase = 'http://localhost:8080/api/utenti';
  private readonly chiaveSessione = 'utenteCorrente';
  private readonly chiaveToken = 'tokenCorrente';
  readonly utenteCorrente = signal<Utente | null>(this.leggiSessione());
  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string): Observable<Utente> {
    return this.http.post<RispostaLogin>(`${this.urlBase}/login`, { email, password }).pipe(
      // Salvo token e utente, poi cambio la risposta nel solo utente
      tap((risposta) => {
        this.salvaToken(risposta.token);
        this.salvaUtente(risposta.utente);
      }),
      map((risposta) => risposta.utente)
    );
  }

  getToken(): string | null {
    try {
      return sessionStorage.getItem(this.chiaveToken);
    } catch {
      return null;
    }
  }

  /* Salva il token nella sessione. */
  private salvaToken(token: string): void {
    try {
      sessionStorage.setItem(this.chiaveToken, token);
    } catch {
    }
  }

  /* Registra un nuovo utente */
  registra(dati: DatiRegistrazione): Observable<Utente> {
    return this.http.post<Utente>(`${this.urlBase}/registrazione`, dati);
  }

  /* Logout ; dimentica l'utente corrente e pulisce la sessione. */
  logout(): void {
    this.utenteCorrente.set(null);
    try {
      sessionStorage.removeItem(this.chiaveSessione);
      sessionStorage.removeItem(this.chiaveToken);
    } catch {
    }
  }

  /* Salva l'utente come corrente ; aggiorna il signal e la sessione. */
  private salvaUtente(utente: Utente): void {
    this.utenteCorrente.set(utente);
    try {
      sessionStorage.setItem(this.chiaveSessione, JSON.stringify(utente));
    } catch {
    }
  }

  /* Recupera l'utente salvato in sessione (o null se non c'è) */
  private leggiSessione(): Utente | null {
    try {
      const grezzo = sessionStorage.getItem(this.chiaveSessione);
      return grezzo ? (JSON.parse(grezzo) as Utente) : null;
    } catch {
      return null;
    }
  }
}
