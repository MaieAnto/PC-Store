/* Modello dell'ordine lato frontend. */
import { Prodotto } from './prodotto';
import { Utente } from './utente';

export interface RigaOrdine {
  id: number;
  prodotto: Prodotto;
  quantita: number;
  prezzoUnitario: number;
}

export interface Ordine {
  id: number;
  dataOrdine: string | number;
  acquirente?: Utente;
  dettagli: RigaOrdine[];
}
