/* Modello del rifornimento lato frontend. */

import { Prodotto } from './prodotto';
import { Utente } from './utente';

/* --- INVIO al backend (richiesta di rifornimento) --- */

/*
 * Una riga della richiesta di rifornimento: indica QUANTE unità aggiungere di
 * un prodotto GIÀ esistente (per id) e a quale costo unitario.
 * Rispecchia il DTO RigaRifornimentoRequest del backend.
 */
export interface RigaRifornimentoRequest {
  idProdotto: number;
  quantita: number;
  costoUnitario: number;
}

/*
 * Corpo della richiesta: solo la lista delle righe. Il fornitore è identificato
 * dall'id nel percorso, la data la genera il database.
 */
export interface RifornimentoRequest {
  righe: RigaRifornimentoRequest[];
}

/* --- RISPOSTA dal backend (storico) --- */

/*
 * Una riga di un rifornimento già registrato (entità DettagliRifornimento).
 * Qui il prodotto arriva completo.
 */
export interface RigaRifornimento {
  id: number;
  prodotto: Prodotto;
  quantita: number;
  costoUnitario: number;
}

/*
 * Un rifornimento registrato.
 *  - dataRifornimento: string | number (data ISO o millisecondi).
 *  - fornitore: chi l'ha registrato (facoltativo qui: nello storico è sempre lui).
 */
export interface Rifornimento {
  id: number;
  dataRifornimento: string | number;
  fornitore?: Utente;
  dettagli: RigaRifornimento[];
}
