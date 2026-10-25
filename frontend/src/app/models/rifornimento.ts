/* Modello del rifornimento lato frontend. */

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
  idProdotto: number;
  nome: string;
  marca: string | null;
  quantita: number;
  costoUnitario: number;
  subtotale: number;
}

export interface Rifornimento {
  id: number;
  dataRifornimento: string | number;
  righe: RigaRifornimento[];
  totaleCosto: number;
}
