package org.example.backend.controllers;

import org.example.backend.dto.RecensioneRequest;
import org.example.backend.entities.Recensione;
import org.example.backend.services.RecensioneService;
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

    // Aggiunge una recensione
    // Consentito solo se l'utente ha acquistato il prodotto, una sola per coppia utente-prodotto
    // Dati nel corpo JSON: { idAutore, idProdotto, valutazione, commento }
    @PostMapping
    public ResponseEntity<Recensione> aggiungiRecensione(@RequestBody RecensioneRequest richiesta) {
        Recensione recensione = recensioneService.aggiungiRecensione(
                richiesta.getIdAutore(), richiesta.getIdProdotto(), richiesta.getValutazione(), richiesta.getCommento());
        return new ResponseEntity<>(recensione, HttpStatus.CREATED);
    }

    // Recensioni di un prodotto, dalla piu' recente alla piu' vecchia
    @GetMapping("/prodotto/{idProdotto}")
    public List<Recensione> recensioniProdotto(@PathVariable int idProdotto) {
        return recensioneService.recensioniProdotto(idProdotto);
    }

    // Media delle valutazioni di un prodotto
    @GetMapping("/prodotto/{idProdotto}/media")
    public double mediaValutazioni(@PathVariable int idProdotto) {
        return recensioneService.mediaValutazioni(idProdotto);
    }

    // Recensioni scritte da un utente
    @GetMapping("/utente/{idAutore}")
    public List<Recensione> recensioniUtente(@PathVariable int idAutore) {
        return recensioneService.recensioniUtente(idAutore);
    }
}
