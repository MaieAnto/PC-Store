import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Recensione } from '../modelli/recensione';

/*
 * Service delle recensioni.
 *
 * Raccoglie le chiamate HTTP verso /api/recensioni: la lettura (elenco e media,
 * usate nella card di dettaglio) e la scrittura di una nuova recensione.
 */
@Injectable({ providedIn: 'root' })
export class RecensioneService {
  private readonly urlBase = 'http://localhost:8080/api/recensioni';

  constructor(private readonly http: HttpClient) {}

  /*
   * Recensioni di un prodotto, dalla più recente alla più vecchia.
   * GET /api/recensioni/prodotto/{idProdotto}
   */
  recensioniProdotto(idProdotto: number): Observable<Recensione[]> {
    return this.http.get<Recensione[]>(`${this.urlBase}/prodotto/${idProdotto}`);
  }

  /*
   * Media delle valutazioni di un prodotto (0 se non ha recensioni).
   * GET /api/recensioni/prodotto/{idProdotto}/media
   */
  mediaValutazioni(idProdotto: number): Observable<number> {
    return this.http.get<number>(`${this.urlBase}/prodotto/${idProdotto}/media`);
  }

  /*
   * Scrive una nuova recensione.
   * POST /api/recensioni con { idProdotto, valutazione, commento } nel corpo
   * JSON (il backend li legge con @RequestBody RecensioneRequest). L'autore non
   * lo mandiamo: lo ricava il backend dal token. Il commento è facoltativo: se
   * vuoto inviamo null.
   *
   * Il backend applica le regole: si può recensire solo un prodotto acquistato,
   * una sola volta, con voto da 1 a 5; altrimenti risponde con un errore che
   * mostreremo all'utente.
   */
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
}
