/* Modello dell'ordine lato frontend. */

export interface RigaOrdine {
  id: number;
  idProdotto: number;
  nome: string;
  marca: string | null;
  immagineUrl: string | null;
  quantita: number;
  prezzoUnitario: number;
  subtotale: number;
}

export interface Ordine {
  id: number;
  dataOrdine: string | number;
  righe: RigaOrdine[];
  totale: number;
}
