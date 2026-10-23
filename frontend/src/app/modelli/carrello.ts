/* Modello del carrello lato frontend. */

import { Prodotto } from './prodotto';

export interface RigaCarrello {
  id: number;
  prodotto: Prodotto;
  quantita: number;
}

export interface Carrello {
  id: number;
  dettagli: RigaCarrello[];
}
