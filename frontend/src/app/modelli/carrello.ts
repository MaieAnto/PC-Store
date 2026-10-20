/* Modello del carrello lato frontend. */

import { Prodotto } from './prodotto';

/*
 * Una RIGA del carrello, cosi' come arriva dal backend (entita'
 * DettagliCarrello serializzata in JSON).
 *
 *  - id:       identificativo della riga nel database.
 *  - prodotto: il prodotto completo (nome, prezzo, immagine e anche
 *              "quantita", che qui e' la GIACENZA in magazzino).
 *  - quantita: quanti pezzi di quel prodotto ci sono NEL CARRELLO.
 *
 * Attenzione a non confondere le due "quantita'": prodotto.quantita e' quanti
 * pezzi esistono in magazzino; riga.quantita e' quanti ne vogliamo comprare.
 */
export interface RigaCarrello {
  id: number;
  prodotto: Prodotto;
  quantita: number;
}

/*
 * Il carrello completo, cosi' come lo restituiscono le operazioni di modifica
 * del backend (aggiungi, aggiorna, rimuovi, svuota).
 *
 * A noi interessa soprattutto l'elenco delle righe (dettagli): dopo ogni
 * modifica aggiorniamo la schermata con questo elenco aggiornato.
 */
export interface Carrello {
  id: number;
  dettagli: RigaCarrello[];
}
