package org.example.backend.eccezioni;

/* Lanciata quando un'operazione non e' consentita: ruolo errato o parametri non validi */
public class OperazioneNonConsentitaException extends RuntimeException {

    public OperazioneNonConsentitaException() {
        super("Operazione non consentita");
    }

    public OperazioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}
