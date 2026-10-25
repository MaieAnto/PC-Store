export const CATEGORIE: string[] = [
  'CPU',
  'RAM',
  'Schede video',
  'Schede madri',
  'Archiviazione',
  'Alimentatori',
  'Case',
];

export interface Prodotto {
  id: number;
  nome: string;
  categoria: string;
  marca: string | null;
  prezzo: number;
  quantita: number;
  immagineUrl: string | null;
  dettagli: string | null;
  disponibile: boolean;
}

export interface PaginaProdotti {
  content: Prodotto[];
  pagina: number;
  dimensione: number;
  totaleElementi: number;
  totalePagine: number;
}

export interface FiltriCatalogo {
  nome?: string;
  marca?: string;
  categoria?: string;
  min?: number | null;
  max?: number | null;
  pagina: number;
  dimensione: number;
  ordinaPer: string;
  direzione: string;
}

export interface NuovoProdotto {
  nome: string;
  categoria: string;
  marca: string | null;
  prezzo: number;
  quantita: number;
  immagineUrl: string | null;
  dettagli: string | null;
}
