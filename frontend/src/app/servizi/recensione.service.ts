import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Recensione } from '../models/recensione';

@Injectable({ providedIn: 'root' })
export class RecensioneService {
  private readonly urlBase = 'http://localhost:8080/api/recensioni';

  constructor(private readonly http: HttpClient) {}

  // Recupera le recensioni di un prodotto.
  recensioniProdotto(idProdotto: number): Observable<Recensione[]> {
    return this.http.get<Recensione[]>(`${this.urlBase}/prodotto/${idProdotto}`);
  }

  // Recupera la media delle valutazioni di un prodotto.
  mediaValutazioni(idProdotto: number): Observable<number> {
    return this.http.get<number>(`${this.urlBase}/prodotto/${idProdotto}/media`);
  }

  // Invia una nuova recensione
  aggiungiRecensione(
    idProdotto: number,
    valutazione: number,
    commento?: string
  ): Observable<Recensione> {
    const commentoPulito = (commento ?? '').trim();
    return this.http.post<Recensione>(this.urlBase, {
      idProdotto,
      valutazione,
      commento: commentoPulito === '' ? null : commentoPulito,
    });
  }

  /* Modifica una recensione esistente */
  aggiornaRecensione(
    id: number,
    valutazione: number,
    commento?: string
  ): Observable<Recensione> {
    const commentoPulito = (commento ?? '').trim();
    return this.http.put<Recensione>(`${this.urlBase}/${id}`, {
      valutazione,
      commento: commentoPulito === '' ? null : commentoPulito,
    });
  }

  /* Elimina una recensione */
  eliminaRecensione(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlBase}/${id}`);
  }
}
