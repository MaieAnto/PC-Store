package org.example.backend.eccezioni;

/* Lanciata quando un'operazione non e' consentita: ruolo errato o parametri non validi
   (es. un FORNITORE che prova ad acquistare, un CLIENTE che prova a rifornire,
   una quantita' minore o uguale a zero). */
public class OperazioneNonConsentitaException extends RuntimeException {

    public OperazioneNonConsentitaException() {
        super("Operazione non consentita");
    }

    public OperazioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}
