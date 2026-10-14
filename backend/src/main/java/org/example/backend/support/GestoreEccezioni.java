package org.example.backend.support;

import org.example.backend.eccezioni.CarrelloVuotoException;
import org.example.backend.eccezioni.CredenzialiNonValideException;
import org.example.backend.eccezioni.EmailGiaRegistrataException;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.OrdineNonTrovatoException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.QuantitaNonDisponibileException;
import org.example.backend.eccezioni.RecensioneGiaEsistenteException;
import org.example.backend.eccezioni.RecensioneNonConsentitaException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestoreEccezioni {

    @ExceptionHandler(CredenzialiNonValideException.class)
    public ResponseEntity<ResponseMessage> credenzialiNonValide(CredenzialiNonValideException e) {
        return risposta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler({UtenteNonTrovatoException.class, ProdottoNonTrovatoException.class, OrdineNonTrovatoException.class})
    public ResponseEntity<ResponseMessage> nonTrovato(RuntimeException e) {
        return risposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({EmailGiaRegistrataException.class, CarrelloVuotoException.class,
            QuantitaNonDisponibileException.class, RecensioneGiaEsistenteException.class})
    public ResponseEntity<ResponseMessage> conflitto(RuntimeException e) {
        return risposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(RecensioneNonConsentitaException.class)
    public ResponseEntity<ResponseMessage> recensioneNonConsentita(RecensioneNonConsentitaException e) {
        return risposta(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({OperazioneNonConsentitaException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseMessage> richiestaNonValida(RuntimeException e) {
        return risposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<ResponseMessage> risposta(HttpStatus stato, String messaggio) {
        return new ResponseEntity<>(new ResponseMessage(messaggio), stato);
    }
}
