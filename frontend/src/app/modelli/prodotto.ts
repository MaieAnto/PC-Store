export interface Categoria {
  etichetta: string;
  endpoint: string | null;
}

export const CATEGORIE: Categoria[] = [
  { etichetta: 'Tutte le categorie', endpoint: null },
  { etichetta: 'CPU', endpoint: 'cpu' },
  { etichetta: 'RAM', endpoint: 'ram' },
  { etichetta: 'Schede video', endpoint: 'schede-video' },
  { etichetta: 'Schede madri', endpoint: 'schede-madri' },
  { etichetta: 'Archiviazione', endpoint: 'archiviazione' },
  { etichetta: 'Alimentatori', endpoint: 'alimentatori' },
  { etichetta: 'Case', endpoint: 'case' },
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
