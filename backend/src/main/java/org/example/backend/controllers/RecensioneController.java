package org.example.backend.controllers;

import org.example.backend.dto.RecensioneRequest;
import org.example.backend.entities.Recensione;
import org.example.backend.services.RecensioneService;
import org.example.backend.services.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    private final RecensioneService recensioneService;
    private final UtenteService utenteService;

    public RecensioneController(RecensioneService recensioneService, UtenteService utenteService) {
        this.recensioneService = recensioneService;
        this.utenteService = utenteService;
    }

    // Aggiunge una recensione solo se l'utente ha acquistato il prodotto, una sola per coppia utente-prodotto
    @PostMapping
    public ResponseEntity<Recensione> aggiungiRecensione(Authentication auth, @RequestBody RecensioneRequest richiesta) {
        int idAutore = utenteService.trovaPerEmail(auth.getName()).getId();
        Recensione recensione = recensioneService.aggiungiRecensione(
                idAutore, richiesta.getIdProdotto(), richiesta.getValutazione(), richiesta.getCommento());
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
}
