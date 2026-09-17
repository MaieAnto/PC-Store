package org.example.backend.eccezioni;

/* Lanciata quando un prodotto richiesto non esiste nel database. */
public class ProdottoNonTrovatoException extends RuntimeException {

    public ProdottoNonTrovatoException() {
        super("Prodotto non trovato");
    }

    public ProdottoNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}
