package org.example.backend.eccezioni;

/* Lanciata quando si tenta di creare un ordine da un carrello vuoto. */
public class CarrelloVuotoException extends RuntimeException {

    public CarrelloVuotoException() {
        super("Il carrello e' vuoto");
    }

    public CarrelloVuotoException(String messaggio) {
        super(messaggio);
    }
}
