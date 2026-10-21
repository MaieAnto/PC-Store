package org.example.backend.eccezioni;

/* Lanciata in fase di registrazione quando l'email e' gia' associata a un utente. */
public class EmailGiaRegistrataException extends RuntimeException {

    public EmailGiaRegistrataException() {
        super("Esiste gia' un utente registrato con questa email");
    }
}
