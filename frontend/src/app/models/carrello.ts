/* Modello del carrello lato frontend. */

export interface RigaCarrello {
  id: number;
  idProdotto: number;
  nome: string;
  marca: string | null;
  categoria: string;
  immagineUrl: string | null;
  prezzo: number;
  quantita: number;             // quantita' nel carrello
  quantitaDisponibile: number;  // scorte del prodotto
  subtotale: number;
}

export interface Carrello {
  id: number | null;
  righe: RigaCarrello[];
  totale: number;
}
