package org.example.backend.controllers;

import org.example.backend.entities.Ordine;
import org.example.backend.services.OrdineService;
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

    // Crea un ordine a partire dal carrello del cliente
    // Controlla le scorte, congela i prezzi, scala il magazzino e svuota il carrello
    // Carrello vuoto / scorta insufficiente -> eccezione
    @PostMapping("/{idUtente}")
    public ResponseEntity<Ordine> creaOrdineDaCarrello(@PathVariable int idUtente) {
        return new ResponseEntity<>(ordineService.creaOrdineDaCarrello(idUtente), HttpStatus.CREATED);
    }

    /* Storico ordini di un cliente, dal piu' recente al piu' vecchio. */
    @GetMapping("/utente/{idUtente}")
    public List<Ordine> storicoOrdini(@PathVariable int idUtente) {
        return ordineService.storicoOrdini(idUtente);
    }

    /* Dettaglio di un singolo ordine. */
    @GetMapping("/{id}")
    public Ordine trovaPerId(@PathVariable int id) {
        return ordineService.trovaPerId(id);
    }
}
