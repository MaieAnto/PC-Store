package org.example.backend.eccezioni;

/* Lanciata quando un utente prova a recensire un prodotto che non ha mai acquistato. */
public class RecensioneNonConsentitaException extends RuntimeException {

    public RecensioneNonConsentitaException() {
        super("Puoi recensire solo i prodotti che hai acquistato");
    }

    public RecensioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}
