package org.example.backend.controllers;

import org.example.backend.dto.NuovoProdotto;
import org.example.backend.dto.PaginaProdotti;
import org.example.backend.entities.Prodotto;
import org.example.backend.services.ProdottoService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    /* Catalogo: tutti i prodotti in vendita, ovvero con disponibile = true */
    @GetMapping
    public List<Prodotto> elencaTutti() {
        return prodottoService.elencaTutti();
    }

    @GetMapping("/catalogo")
    public PaginaProdotti catalogo(@RequestParam(required = false) String nome, @RequestParam(required = false) String marca,
                                   @RequestParam(required = false) String categoria, @RequestParam(required = false) Double min,
                                   @RequestParam(required = false) Double max, @RequestParam(defaultValue = "0") int pagina,
                                   @RequestParam(defaultValue = "12") int dimensione, @RequestParam(defaultValue = "nome") String ordinaPer,
                                   @RequestParam(defaultValue = "asc") String direzione) {
        return new PaginaProdotti(prodottoService.cercaCatalogoPaginato(nome, marca, categoria, min, max, pagina, dimensione, ordinaPer, direzione));
    }

    // Dettaglio di un singolo prodotto
    @GetMapping("/{id}")
    public Prodotto trovaPerId(@PathVariable int id) {
        return prodottoService.trovaPerId(id);
    }

    // Inserimento di un nuovo prodotto
    @PostMapping
    public ResponseEntity<Prodotto> aggiungi(@RequestBody NuovoProdotto dati) {
        return new ResponseEntity<>(prodottoService.aggiungiProdotto(dati), HttpStatus.CREATED);
    }

    // Modifica di un prodotto esistente
    @PutMapping("/{id}")
    public Prodotto aggiorna(@PathVariable int id, @RequestBody NuovoProdotto dati) {
        return prodottoService.aggiornaProdotto(id, dati);
    }

    // Eliminazione di un prodotto dal catalogo ma resta nello storico
    @DeleteMapping("/{id}")
    public ResponseMessage eliminaProdotto(@PathVariable int id) {
        prodottoService.eliminaProdotto(id);
        return new ResponseMessage("Prodotto eliminato");
    }

    // Prodotti eliminati (disponibile = false)
    @GetMapping("/eliminati")
    public List<Prodotto> elencaEliminati() {
        return prodottoService.elencaEliminati();
    }

    // Rimette in vendita un prodotto eliminato
    @PutMapping("/{id}/ripristina")
    public Prodotto ripristinaProdotto(@PathVariable int id) {
        return prodottoService.ripristinaProdotto(id);
    }
}
