/* Modello della recensione lato frontend. */

import { Utente } from './utente';

export interface Recensione {
  id: number;
  autore: Utente;
  valutazione: number;
  commento: string | null;
  dataRecensione: string | number;
}
