package org.example.backend.controllers;

import org.example.backend.entities.Recensione;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.RecensioneGiaEsistenteException;
import org.example.backend.eccezioni.RecensioneNonConsentitaException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.example.backend.services.RecensioneService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    private final RecensioneService recensioneService;

    public RecensioneController(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    /* Aggiunge una recensione (consentita solo se l'utente ha acquistato il prodotto,
       una sola per coppia utente-prodotto, valutazione da 1 a 5). */
    @PostMapping
    public ResponseEntity<?> aggiungiRecensione(@RequestParam int idAutore,
                                                @RequestParam int idProdotto,
                                                @RequestParam int valutazione,
                                                @RequestParam(required = false) String commento) {
        try {
            Recensione recensione = recensioneService.aggiungiRecensione(idAutore, idProdotto, valutazione, commento);
            return new ResponseEntity<>(recensione, HttpStatus.CREATED);
        } catch (UtenteNonTrovatoException | ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (RecensioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (RecensioneGiaEsistenteException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Recensioni di un prodotto, dalla piu' recente alla piu' vecchia. */
    @GetMapping("/prodotto/{idProdotto}")
    public ResponseEntity<List<Recensione>> recensioniProdotto(@PathVariable int idProdotto) {
        return new ResponseEntity<>(recensioneService.recensioniProdotto(idProdotto), HttpStatus.OK);
    }

    /* Media delle valutazioni di un prodotto (0 se non ha recensioni). */
    @GetMapping("/prodotto/{idProdotto}/media")
    public ResponseEntity<Double> mediaValutazioni(@PathVariable int idProdotto) {
        return new ResponseEntity<>(recensioneService.mediaValutazioni(idProdotto), HttpStatus.OK);
    }

    /* Recensioni scritte da un utente. */
    @GetMapping("/utente/{idAutore}")
    public ResponseEntity<?> recensioniUtente(@PathVariable int idAutore) {
        try {
            return new ResponseEntity<>(recensioneService.recensioniUtente(idAutore), HttpStatus.OK);
        } catch (UtenteNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
