package org.example.backend.eccezioni;

/* Lanciata in fase di login quando email o password non sono corrette. */
public class CredenzialiNonValideException extends RuntimeException {

    public CredenzialiNonValideException() {
        super("Email o password non corretti");
    }

    public CredenzialiNonValideException(String messaggio) {
        super(messaggio);
    }
}
