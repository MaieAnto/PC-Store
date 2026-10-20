/* Modello della recensione lato frontend. */

import { Utente } from './utente';

/*
 * Una recensione di un prodotto, così come arriva dal backend.
 *
 *  - autore:         l'utente che l'ha scritta (ci servono nome e cognome).
 *  - valutazione:    voto da 1 a 5.
 *  - commento:       testo facoltativo (può essere null).
 *  - dataRecensione: quando è stata scritta. La lasciamo come string | number
 *                    perché a seconda della configurazione il backend può
 *                    inviare una data ISO ("2026-09-19T...") o i millisecondi:
 *                    in entrambi i casi `new Date(valore)` la interpreta bene.
 *
 * Nota: il JSON include anche il prodotto, ma qui non lo dichiariamo perché nel
 * dettaglio il prodotto lo abbiamo già; i campi in più vengono semplicemente
 * ignorati.
 */
export interface Recensione {
  id: number;
  autore: Utente;
  valutazione: number;
  commento: string | null;
  dataRecensione: string | number;
}
