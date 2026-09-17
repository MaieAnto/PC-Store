package org.example.backend.eccezioni;

/* Lanciata quando la scorta di un prodotto non e' sufficiente per l'operazione richiesta. */
public class QuantitaNonDisponibileException extends RuntimeException {

    public QuantitaNonDisponibileException() {
        super("Quantita' non disponibile a magazzino");
    }

    public QuantitaNonDisponibileException(String messaggio) {
        super(messaggio);
    }
}
