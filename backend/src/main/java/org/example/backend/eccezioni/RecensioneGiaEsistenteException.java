package org.example.backend.eccezioni;

/* Lanciata quando un utente prova a recensire due volte lo stesso prodotto. */
public class RecensioneGiaEsistenteException extends RuntimeException {

    public RecensioneGiaEsistenteException() {
        super("Hai gia' recensito questo prodotto");
    }
}
