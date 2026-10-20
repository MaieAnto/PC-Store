/* Modello dell'utente lato frontend */

export enum Ruolo {
  CLIENTE = 'CLIENTE',
  FORNITORE = 'FORNITORE',
}

// La struttura di un utente che arriva dal backend.
export interface Utente {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  ruolo: Ruolo;
}

// Dati inviati al backend per registrare un nuovo utente
export interface DatiRegistrazione {
  nome: string;
  cognome: string;
  email: string;
  password: string;
  ruolo: Ruolo;
}

// Risposta del login: il token JWT da riallegare alle richieste + l'utente.
export interface RispostaLogin {
  token: string;
  utente: Utente;
}
