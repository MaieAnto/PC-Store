package org.example.backend.eccezioni;

/* Lanciata quando si tenta di inserire un prodotto con un codice gia' presente. */
public class CodiceProdottoGiaEsistenteException extends RuntimeException {

    public CodiceProdottoGiaEsistenteException() {
        super("Esiste gia' un prodotto con questo codice");
    }

    public CodiceProdottoGiaEsistenteException(String messaggio) {
        super(messaggio);
    }
}
