package org.example.backend.controllers;

import org.example.backend.entities.Ordine;
import org.example.backend.eccezioni.CarrelloVuotoException;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.OrdineNonTrovatoException;
import org.example.backend.eccezioni.QuantitaNonDisponibileException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.example.backend.services.OrdineService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    /* Crea un ordine a partire dal carrello del cliente (operazione atomica:
       controlla le scorte, congela i prezzi, scala il magazzino, svuota il carrello). */
    @PostMapping("/{idUtente}")
    public ResponseEntity<?> creaOrdineDaCarrello(@PathVariable int idUtente) {
        try {
            Ordine ordine = ordineService.creaOrdineDaCarrello(idUtente);
            return new ResponseEntity<>(ordine, HttpStatus.CREATED);
        } catch (UtenteNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (CarrelloVuotoException | QuantitaNonDisponibileException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /* Storico ordini di un cliente, dal piu' recente al piu' vecchio. */
    @GetMapping("/utente/{idUtente}")
    public ResponseEntity<List<Ordine>> storicoOrdini(@PathVariable int idUtente) {
        return new ResponseEntity<>(ordineService.storicoOrdini(idUtente), HttpStatus.OK);
    }

    /* Dettaglio di un singolo ordine. */
    @GetMapping("/{id}")
    public ResponseEntity<?> trovaPerId(@PathVariable int id) {
        try {
            return new ResponseEntity<>(ordineService.trovaPerId(id), HttpStatus.OK);
        } catch (OrdineNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
