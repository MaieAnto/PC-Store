import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ordine } from '../models/ordine';

@Injectable({ providedIn: 'root' })
export class OrdineService {
  private readonly urlBase = 'http://localhost:8080/api/ordini';

  constructor(private readonly http: HttpClient) {}

  // Crea un ordine dal carrello dell'utente loggato.
  creaOrdine(): Observable<Ordine> {
    return this.http.post<Ordine>(this.urlBase, null);
  }

  // Recupera lo storico degli ordini dell'utente loggato.
  storicoOrdini(): Observable<Ordine[]> {
    return this.http.get<Ordine[]>(`${this.urlBase}/utente`);
  }
}
