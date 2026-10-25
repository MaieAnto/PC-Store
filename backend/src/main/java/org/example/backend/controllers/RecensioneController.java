package org.example.backend.controllers;

import org.example.backend.dto.output.RecensioneDTO;
import org.example.backend.dto.input.RecensioneRequest;
import org.example.backend.entities.Recensione;
import org.example.backend.services.RecensioneService;
import org.example.backend.support.UtenteCorrente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    private final RecensioneService recensioneService;
    private final UtenteCorrente utenteCorrente;

    public RecensioneController(RecensioneService recensioneService, UtenteCorrente utenteCorrente) {
        this.recensioneService = recensioneService;
        this.utenteCorrente = utenteCorrente;
    }

    // Aggiunge una recensione solo se l'utente ha acquistato il prodotto
    @PostMapping
    public ResponseEntity<RecensioneDTO> aggiungiRecensione(Authentication auth, @RequestBody RecensioneRequest richiesta) {
        int idAutore = utenteCorrente.id(auth);
        Recensione recensione = recensioneService.aggiungiRecensione(
                idAutore, richiesta.getIdProdotto(), richiesta.getValutazione(), richiesta.getCommento());
        return new ResponseEntity<>(new RecensioneDTO(recensione), HttpStatus.CREATED);
    }

    // Modifica una recensione
    @PutMapping("/{id}")
    public RecensioneDTO aggiornaRecensione(Authentication auth, @PathVariable int id,
                                         @RequestBody RecensioneRequest richiesta) {
        int idAutore = utenteCorrente.id(auth);
        Recensione recensione = recensioneService.aggiornaRecensione(
                idAutore, id, richiesta.getValutazione(), richiesta.getCommento());
        return new RecensioneDTO(recensione);
    }

    // Elimina una recensione
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaRecensione(Authentication auth, @PathVariable int id) {
        recensioneService.eliminaRecensione(utenteCorrente.id(auth), id);
        return ResponseEntity.noContent().build();
    }

    // Recensioni di un prodotto, dalla piu' recente alla piu' vecchia
    @GetMapping("/prodotto/{idProdotto}")
    public List<RecensioneDTO> recensioniProdotto(@PathVariable int idProdotto) {
        return recensioneService.recensioniProdotto(idProdotto).stream()
                .map(RecensioneDTO::new)
                .collect(Collectors.toList());
    }

    // Media delle valutazioni di un prodotto
    @GetMapping("/prodotto/{idProdotto}/media")
    public double mediaValutazioni(@PathVariable int idProdotto) {
        return recensioneService.mediaValutazioni(idProdotto);
    }
}
