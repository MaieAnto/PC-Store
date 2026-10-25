package org.example.backend.eccezioni;

/* Lanciata quando una recensione richiesta non esiste nel database. */
public class RecensioneNonTrovataException extends RuntimeException {

    public RecensioneNonTrovataException() {
        super("Recensione non trovata");
    }

    public RecensioneNonTrovataException(String messaggio) {
        super(messaggio);
    }
}
