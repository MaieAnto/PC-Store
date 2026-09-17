package org.example.backend.eccezioni;

/* Lanciata quando un utente richiesto non esiste nel database. */
public class UtenteNonTrovatoException extends RuntimeException {

    public UtenteNonTrovatoException() {
        super("Utente non trovato");
    }

    public UtenteNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}
