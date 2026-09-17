package org.example.backend.eccezioni;

/* Lanciata quando un ordine richiesto non esiste nel database. */
public class OrdineNonTrovatoException extends RuntimeException {

    public OrdineNonTrovatoException() {
        super("Ordine non trovato");
    }

    public OrdineNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}
