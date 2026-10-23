import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ordine } from '../modelli/ordine';

/*
 * Service degli ordini.
 *
 * Raccoglie le chiamate HTTP verso /api/ordini. Il cliente crea un ordine a
 * partire dal proprio carrello e può consultare il suo storico.
 */
@Injectable({ providedIn: 'root' })
export class OrdineService {
  private readonly urlBase = 'http://localhost:8080/api/ordini';

  constructor(private readonly http: HttpClient) {}

  /*
   * Crea un ordine dal carrello del cliente (checkout).
   * POST /api/ordini
   *
   * È un'operazione atomica lato backend: controlla le scorte, congela i
   * prezzi, scala il magazzino e svuota il carrello. Non serve corpo né id:
   * il backend ricava il cliente dal token e usa il suo carrello attuale.
   */
  creaOrdine(): Observable<Ordine> {
    return this.http.post<Ordine>(this.urlBase, null);
  }

  /*
   * Storico ordini del cliente loggato, dal più recente al più vecchio.
   * GET /api/ordini/utente (il cliente è identificato dal token).
   */
  storicoOrdini(): Observable<Ordine[]> {
    return this.http.get<Ordine[]>(`${this.urlBase}/utente`);
  }
}
