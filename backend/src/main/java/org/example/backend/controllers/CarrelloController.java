package org.example.backend.controllers;

import org.example.backend.dto.RigaCarrelloRequest;
import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.services.CarrelloService;
import org.example.backend.services.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final UtenteService utenteService;

    public CarrelloController(CarrelloService carrelloService, UtenteService utenteService) {
        this.carrelloService = carrelloService;
        this.utenteService = utenteService;
    }

    /* L'id dell'utente non arriva piu' dall'URL: lo ricaviamo dall'email
       contenuta nel token (auth.getName()), cosi' ognuno agisce solo sul
       proprio carrello. */
    private int idUtenteLoggato(Authentication auth) {
        return utenteService.trovaPerEmail(auth.getName()).getId();
    }

    /* Righe del carrello dell'utente loggato (sola lettura, non crea il carrello). */
    @GetMapping
    public List<DettagliCarrello> getDettagli(Authentication auth) {
        return carrelloService.getDettagli(idUtenteLoggato(auth));
    }

    /* Aggiunge un prodotto al carrello (o incrementa la quantita' se gia' presente).
       Dati nel corpo JSON: { idProdotto, quantita }. */
    @PostMapping("/aggiungi")
    public Carrello aggiungiAlCarrello(Authentication auth, @RequestBody RigaCarrelloRequest riga) {
        return carrelloService.aggiungiAlCarrello(idUtenteLoggato(auth), riga.getIdProdotto(), riga.getQuantita());
    }

    /* Imposta una quantita' precisa per un prodotto gia' nel carrello.
       Dati nel corpo JSON: { idProdotto, quantita }. */
    @PutMapping("/quantita")
    public Carrello aggiornaQuantita(Authentication auth, @RequestBody RigaCarrelloRequest riga) {
        return carrelloService.aggiornaQuantita(idUtenteLoggato(auth), riga.getIdProdotto(), riga.getQuantita());
    }

    /* Rimuove un prodotto dal carrello. */
    @DeleteMapping("/prodotto/{idProdotto}")
    public Carrello rimuoviDalCarrello(Authentication auth, @PathVariable int idProdotto) {
        return carrelloService.rimuoviDalCarrello(idUtenteLoggato(auth), idProdotto);
    }

    /* Svuota completamente il carrello dell'utente. */
    @DeleteMapping
    public Carrello svuotaCarrello(Authentication auth) {
        return carrelloService.svuotaCarrello(idUtenteLoggato(auth));
    }
}
