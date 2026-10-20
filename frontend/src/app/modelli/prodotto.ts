/* Modello del prodotto lato frontend */

/*
 * Una "categoria" di prodotto mostrata nel filtro del catalogo e nel form di
 * inserimento.
 *
 *  - etichetta: il testo leggibile mostrato all'utente. Coincide con
 *               l'etichetta che il backend usa nel JSON per la categoria.
 *  - endpoint:  chiave tecnica della categoria (es. 'cpu'). NON serve a
 *               costruire URL (c'è un unico endpoint /api/prodotti): la usiamo
 *               solo come valore stabile nei menu a tendina. Vale null per
 *               "Tutte le categorie", voce usata solo dal filtro del catalogo.
 */
export interface Categoria {
  etichetta: string;
  endpoint: string | null;
}

/*
 * Elenco delle categorie disponibili. L'ordine qui e' l'ordine in cui
 * compaiono nel menu. La prima voce (endpoint null) e' quella predefinita
 * e mostra tutti i prodotti.
 *
 * Le etichette combaciano ESATTAMENTE con quelle dell'enum Categoria del
 * backend, così il filtro del catalogo (che confronta la categoria) e
 * l'inserimento funzionano senza conversioni.
 */
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

/*
 * La struttura di un prodotto cosi' come arriva dal backend.
 *
 * Prodotto è unico (niente sottoclassi): la categoria è un campo e i dettagli
 * tecnici sono un semplice testo libero.
 *
 * Alcuni campi possono essere null perche' sul backend sono opzionali
 * (nullable = true): marca, immagineUrl e dettagli.
 */
export interface Prodotto {
  id: number;
  nome: string;
  // Categoria leggibile (es. "CPU", "Schede video"): usata per il filtro e il
  // badge nel catalogo.
  categoria: string;
  marca: string | null;
  prezzo: number;
  quantita: number;
  immagineUrl: string | null;
  // Dettagli tecnici come testo libero (mostrati nella card del prodotto).
  dettagli: string | null;
  // In vendita? false = prodotto "eliminato" dal fornitore (eliminazione
  // logica): non compare nel catalogo, ma resta negli ordini già fatti.
  // Da non confondere con la giacenza (quantita), che decide "Esaurito".
  disponibile: boolean;
}

/*
 * Una "pagina" di prodotti restituita dal catalogo paginato.
 * Rispecchia il DTO PaginaProdotti del backend (GET /api/prodotti/catalogo):
 * contiene i prodotti della pagina corrente piu' i dati che servono al
 * paginatore (quale pagina, quanti per pagina, totali).
 */
export interface PaginaProdotti {
  content: Prodotto[];      // prodotti di questa pagina
  pagina: number;           // indice pagina corrente (0 = prima)
  dimensione: number;       // prodotti per pagina
  totaleElementi: number;   // totale prodotti che rispettano i filtri
  totalePagine: number;     // numero complessivo di pagine
}

/*
 * Filtri + paginazione + ordinamento inviati al catalogo.
 * I campi testuali/numerici sono opzionali: se assenti, il backend non applica
 * quel filtro.
 */
export interface FiltriCatalogo {
  nome?: string;
  marca?: string;
  categoria?: string;       // etichetta ('CPU', 'Schede video', ...) o vuoto = tutte
  min?: number | null;
  max?: number | null;
  pagina: number;
  dimensione: number;
  ordinaPer: string;        // 'nome' | 'prezzo' | 'marca'
  direzione: string;        // 'asc' | 'desc'
}

/*
 * Dati inviati al backend per aggiungere o modificare un prodotto.
 *
 * I dettagli tecnici sono una semplice stringa. La categoria è l'etichetta
 * scelta nel form.
 *
 * Nota: l'id non c'e' perche' lo assegna il database.
 */
export interface NuovoProdotto {
  nome: string;
  categoria: string;
  marca: string | null;
  prezzo: number;
  quantita: number;
  immagineUrl: string | null;
  dettagli: string | null;
}
