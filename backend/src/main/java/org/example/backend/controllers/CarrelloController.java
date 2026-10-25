package org.example.backend.controllers;

import org.example.backend.dto.output.CarrelloDTO;
import org.example.backend.dto.input.RigaCarrelloRequest;
import org.example.backend.services.CarrelloService;
import org.example.backend.support.UtenteCorrente;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final UtenteCorrente utenteCorrente;

    public CarrelloController(CarrelloService carrelloService, UtenteCorrente utenteCorrente) {
        this.carrelloService = carrelloService;
        this.utenteCorrente = utenteCorrente;
    }

    /* Righe del carrello dell'utente corrente */
    @GetMapping
    public CarrelloDTO getDettagli(Authentication auth) {
        return new CarrelloDTO(carrelloService.getDettagli(utenteCorrente.id(auth)));
    }

    /* Aggiunge un prodotto al carrello (o incrementa la quantita' se gia' presente) */
    @PostMapping("/aggiungi")
    public CarrelloDTO aggiungiAlCarrello(Authentication auth, @RequestBody RigaCarrelloRequest riga) {
        return new CarrelloDTO(carrelloService.aggiungiAlCarrello(utenteCorrente.id(auth), riga.getIdProdotto(), riga.getQuantita()));
    }

    /* Imposta una quantita' precisa per un prodotto gia' nel carrello */
    @PutMapping("/quantita")
    public CarrelloDTO aggiornaQuantita(Authentication auth, @RequestBody RigaCarrelloRequest riga) {
        return new CarrelloDTO(carrelloService.aggiornaQuantita(utenteCorrente.id(auth), riga.getIdProdotto(), riga.getQuantita()));
    }

    /* Rimuove un prodotto dal carrello. */
    @DeleteMapping("/prodotto/{idProdotto}")
    public CarrelloDTO rimuoviDalCarrello(Authentication auth, @PathVariable int idProdotto) {
        return new CarrelloDTO(carrelloService.rimuoviDalCarrello(utenteCorrente.id(auth), idProdotto));
    }

    /* Svuota completamente il carrello */
    @DeleteMapping
    public CarrelloDTO svuotaCarrello(Authentication auth) {
        return new CarrelloDTO(carrelloService.svuotaCarrello(utenteCorrente.id(auth)));
    }
}
