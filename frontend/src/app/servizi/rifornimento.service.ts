import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Rifornimento, RifornimentoRequest } from '../models/rifornimento';

@Injectable({ providedIn: 'root' })
export class RifornimentoService {
  private readonly urlBase = 'http://localhost:8080/api/rifornimenti';

  constructor(private readonly http: HttpClient) {}

  // Invia un nuovo rifornimento
  creaRifornimento(richiesta: RifornimentoRequest): Observable<Rifornimento> {
    return this.http.post<Rifornimento>(this.urlBase, richiesta);
  }

  // Recupera lo storico dei rifornimenti del fornitore corrente
  storicoRifornimenti(): Observable<Rifornimento[]> {
    return this.http.get<Rifornimento[]>(`${this.urlBase}/fornitore`);
  }
}
