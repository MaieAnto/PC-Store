package org.example.backend.controllers;

import org.example.backend.dto.RigaCarrelloRequest;
import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.services.CarrelloService;
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
    public List<DettagliCarrello> getDettagli(@PathVariable int idUtente) {
        return carrelloService.getDettagli(idUtente);
    }

    /* Aggiunge un prodotto al carrello (o incrementa la quantita' se gia' presente).
       Dati nel corpo JSON: { idProdotto, quantita }. */
    @PostMapping("/{idUtente}/aggiungi")
    public Carrello aggiungiAlCarrello(@PathVariable int idUtente, @RequestBody RigaCarrelloRequest riga) {
        return carrelloService.aggiungiAlCarrello(idUtente, riga.getIdProdotto(), riga.getQuantita());
    }

    /* Imposta una quantita' precisa per un prodotto gia' nel carrello.
       Dati nel corpo JSON: { idProdotto, quantita }. */
    @PutMapping("/{idUtente}/quantita")
    public Carrello aggiornaQuantita(@PathVariable int idUtente, @RequestBody RigaCarrelloRequest riga) {
        return carrelloService.aggiornaQuantita(idUtente, riga.getIdProdotto(), riga.getQuantita());
    }

    /* Rimuove un prodotto dal carrello. */
    @DeleteMapping("/{idUtente}/prodotto/{idProdotto}")
    public Carrello rimuoviDalCarrello(@PathVariable int idUtente, @PathVariable int idProdotto) {
        return carrelloService.rimuoviDalCarrello(idUtente, idProdotto);
    }

    /* Svuota completamente il carrello dell'utente. */
    @DeleteMapping("/{idUtente}")
    public Carrello svuotaCarrello(@PathVariable int idUtente) {
        return carrelloService.svuotaCarrello(idUtente);
    }
}
