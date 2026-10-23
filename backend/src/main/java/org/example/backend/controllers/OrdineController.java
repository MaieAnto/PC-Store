package org.example.backend.controllers;

import org.example.backend.entities.Ordine;
import org.example.backend.services.OrdineService;
import org.example.backend.services.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {

    private final OrdineService ordineService;
    private final UtenteService utenteService;

    public OrdineController(OrdineService ordineService, UtenteService utenteService) {
        this.ordineService = ordineService;
        this.utenteService = utenteService;
    }

    /* L'id del cliente lo ricaviamo dal token (email -> utente), non dall'URL:
       cosi' si puo' ordinare e vedere lo storico solo per se stessi. */
    private int idUtenteLoggato(Authentication auth) {
        return utenteService.trovaPerEmail(auth.getName()).getId();
    }

    // Crea un ordine a partire dal carrello del cliente loggato
    // Controlla le scorte, congela i prezzi, scala il magazzino e svuota il carrello
    // Carrello vuoto / scorta insufficiente -> eccezione
    @PostMapping
    public ResponseEntity<Ordine> creaOrdineDaCarrello(Authentication auth) {
        return new ResponseEntity<>(ordineService.creaOrdineDaCarrello(idUtenteLoggato(auth)), HttpStatus.CREATED);
    }

    /* Storico ordini del cliente loggato, dal piu' recente al piu' vecchio. */
    @GetMapping("/utente")
    public List<Ordine> storicoOrdini(Authentication auth) {
        return ordineService.storicoOrdini(idUtenteLoggato(auth));
    }
}
