export enum Ruolo {
  CLIENTE = 'CLIENTE',
  FORNITORE = 'FORNITORE',
}

export interface Utente {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  ruolo: Ruolo;
}

export interface DatiRegistrazione {
  nome: string;
  cognome: string;
  email: string;
  password: string;
  ruolo: Ruolo;
}

export interface RispostaLogin {
  token: string;
  utente: Utente;
}
