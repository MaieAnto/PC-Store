import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FiltriCatalogo, NuovoProdotto, PaginaProdotti, Prodotto } from '../modelli/prodotto';


@Injectable({ providedIn: 'root' })
export class ProdottoService {

  private readonly urlBase = 'http://localhost:8080/api/prodotti';

  constructor(private readonly http: HttpClient) {}

  elencaTutti(): Observable<Prodotto[]> {
    return this.http.get<Prodotto[]>(this.urlBase);
  }

  cercaCatalogo(filtri: FiltriCatalogo): Observable<PaginaProdotti> {
    let parametri = new HttpParams()
      .set('pagina', filtri.pagina)
      .set('dimensione', filtri.dimensione)
      .set('ordinaPer', filtri.ordinaPer)
      .set('direzione', filtri.direzione);

    if (filtri.nome) {
      parametri = parametri.set('nome', filtri.nome);
    }
    if (filtri.marca) {
      parametri = parametri.set('marca', filtri.marca);
    }
    if (filtri.categoria) {
      parametri = parametri.set('categoria', filtri.categoria);
    }
    if (filtri.min != null) {
      parametri = parametri.set('min', filtri.min);
    }
    if (filtri.max != null) {
      parametri = parametri.set('max', filtri.max);
    }

    return this.http.get<PaginaProdotti>(`${this.urlBase}/catalogo`, { params: parametri });
  }

  /* Dettaglio di un singolo prodotto  */
  trovaPerId(id: number): Observable<Prodotto> {
    return this.http.get<Prodotto>(`${this.urlBase}/${id}`);
  }

  /* Aggiunge un nuovo prodotto  */
  aggiungi(prodotto: NuovoProdotto): Observable<Prodotto> {
    return this.http.post<Prodotto>(this.urlBase, prodotto);
  }

  /*
   * Modifica un prodotto esistente */
  aggiorna(id: number, prodotto: NuovoProdotto): Observable<Prodotto> {
    return this.http.put<Prodotto>(`${this.urlBase}/${id}`, prodotto);
  }

  /* Elimina un prodotto dal catalogo  */
  elimina(id: number): Observable<{ messaggio: string }> {
    return this.http.delete<{ messaggio: string }>(`${this.urlBase}/${id}`);
  }

  /* Prodotti eliminati (non più in vendita)  */
  elencaEliminati(): Observable<Prodotto[]> {
    return this.http.get<Prodotto[]>(`${this.urlBase}/eliminati`);
  }

  /* Rimette in vendita un prodotto eliminato  */
  ripristina(id: number): Observable<Prodotto> {
    return this.http.put<Prodotto>(`${this.urlBase}/${id}/ripristina`, null);
  }
}
