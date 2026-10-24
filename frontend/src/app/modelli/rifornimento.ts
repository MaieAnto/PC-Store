/* Modello del rifornimento lato frontend. */

import { Prodotto } from './prodotto';
import { Utente } from './utente';

export interface RigaRifornimentoRequest {
  idProdotto: number;
  quantita: number;
  costoUnitario: number;
}

export interface RifornimentoRequest {
  righe: RigaRifornimentoRequest[];
}

export interface RigaRifornimento {
  id: number;
  prodotto: Prodotto;
  quantita: number;
  costoUnitario: number;
}

export interface Rifornimento {
  id: number;
  dataRifornimento: string | number;
  fornitore?: Utente;
  dettagli: RigaRifornimento[];
}
