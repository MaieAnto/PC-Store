/* Modello dell'ordine lato frontend. */

import { Prodotto } from './prodotto';
import { Utente } from './utente';

/*
 * Una riga di un ordine (entità DettagliOrdine del backend).
 *
 *  - prodotto:       il prodotto acquistato.
 *  - quantita:       quanti pezzi sono stati ordinati.
 *  - prezzoUnitario: il prezzo "congelato" al momento dell'ordine. È importante
 *                    perché il prezzo del prodotto può cambiare dopo: l'ordine
 *                    conserva quello pagato davvero.
 */
export interface RigaOrdine {
  id: number;
  prodotto: Prodotto;
  quantita: number;
  prezzoUnitario: number;
}

/*
 * Un ordine, così come arriva dal backend.
 *
 *  - dataOrdine: quando è stato creato. La lasciamo come string | number
 *                (data ISO o millisecondi): `new Date(valore)` gestisce entrambi.
 *  - acquirente: il cliente (facoltativo qui: nello storico è sempre lo stesso).
 *  - dettagli:   le righe dell'ordine.
 */
export interface Ordine {
  id: number;
  dataOrdine: string | number;
  acquirente?: Utente;
  dettagli: RigaOrdine[];
}
