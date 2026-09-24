package org.example.backend.controllers;

import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.QuantitaNonDisponibileException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.example.backend.services.CarrelloService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;

    public CarrelloController(CarrelloService carrelloService) {
        this.carrelloService = carrelloService;
    }

    /* Righe del carrello dell'utente (sola lettura, non crea il carrello). */
    @GetMapping("/{idUtente}")
    public ResponseEntity<List<DettagliCarrello>> getDettagli(@PathVariable int idUtente) {
        return new ResponseEntity<>(carrelloService.getDettagli(idUtente), HttpStatus.OK);
    }

    /* Aggiunge un prodotto al carrello (o incrementa la quantita' se gia' presente). */
    @PostMapping("/{idUtente}/aggiungi")
    public ResponseEntity<?> aggiungiAlCarrello(@PathVariable int idUtente,
                                                @RequestParam int idProdotto,
                                                @RequestParam int quantita) {
        try {
            Carrello carrello = carrelloService.aggiungiAlCarrello(idUtente, idProdotto, quantita);
            return new ResponseEntity<>(carrello, HttpStatus.OK);
        } catch (UtenteNonTrovatoException | ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (QuantitaNonDisponibileException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Imposta una quantita' precisa per un prodotto gia' nel carrello. */
    @PutMapping("/{idUtente}/quantita")
    public ResponseEntity<?> aggiornaQuantita(@PathVariable int idUtente,
                                              @RequestParam int idProdotto,
                                              @RequestParam int nuovaQuantita) {
        try {
            Carrello carrello = carrelloService.aggiornaQuantita(idUtente, idProdotto, nuovaQuantita);
            return new ResponseEntity<>(carrello, HttpStatus.OK);
        } catch (UtenteNonTrovatoException | ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (QuantitaNonDisponibileException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Rimuove un prodotto dal carrello. */
    @DeleteMapping("/{idUtente}/prodotto/{idProdotto}")
    public ResponseEntity<?> rimuoviDalCarrello(@PathVariable int idUtente,
                                                @PathVariable int idProdotto) {
        try {
            Carrello carrello = carrelloService.rimuoviDalCarrello(idUtente, idProdotto);
            return new ResponseEntity<>(carrello, HttpStatus.OK);
        } catch (UtenteNonTrovatoException | ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Svuota completamente il carrello dell'utente. */
    @DeleteMapping("/{idUtente}")
    public ResponseEntity<?> svuotaCarrello(@PathVariable int idUtente) {
        try {
            Carrello carrello = carrelloService.svuotaCarrello(idUtente);
            return new ResponseEntity<>(carrello, HttpStatus.OK);
        } catch (UtenteNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
