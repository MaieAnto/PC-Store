/* Modello della recensione lato frontend. */

export interface Recensione {
  id: number;
  idAutore: number;
  autore: string;
  valutazione: number;
  commento: string | null;
  dataRecensione: string | number;
}
