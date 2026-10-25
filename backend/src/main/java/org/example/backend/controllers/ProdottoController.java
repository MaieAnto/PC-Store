package org.example.backend.controllers;

import org.example.backend.dto.input.NuovoProdotto;
import org.example.backend.dto.output.PaginaProdotti;
import org.example.backend.dto.output.ProdottoDTO;
import org.example.backend.entities.Prodotto;
import org.example.backend.services.ProdottoService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GetMapping("/catalogo")
    public PaginaProdotti catalogo(@RequestParam(required = false) String nome, @RequestParam(required = false) String marca,
                                   @RequestParam(required = false) String categoria, @RequestParam(required = false) Double min,
                                   @RequestParam(required = false) Double max, @RequestParam(defaultValue = "0") int pagina,
                                   @RequestParam(defaultValue = "12") int dimensione, @RequestParam(defaultValue = "nome") String ordinaPer,
                                   @RequestParam(defaultValue = "asc") String direzione) {
        return new PaginaProdotti(prodottoService.cercaCatalogoPaginato(nome, marca, categoria, min, max, pagina, dimensione, ordinaPer, direzione));
    }

    // Elenco di tutti i prodotti in vendita (senza paginazione), usato dalle pagine del fornitore
    @GetMapping
    public List<ProdottoDTO> elencaTutti() {
        return aDTO(prodottoService.elencaTutti());
    }

    // Dettaglio di un singolo prodotto
    @GetMapping("/{id}")
    public ProdottoDTO trovaPerId(@PathVariable int id) {
        return new ProdottoDTO(prodottoService.trovaPerId(id));
    }

    // Inserimento di un nuovo prodotto
    @PostMapping
    public ResponseEntity<ProdottoDTO> aggiungi(@RequestBody NuovoProdotto dati) {
        return new ResponseEntity<>(new ProdottoDTO(prodottoService.aggiungiProdotto(dati)), HttpStatus.CREATED);
    }

    // Modifica di un prodotto esistente
    @PutMapping("/{id}")
    public ProdottoDTO aggiorna(@PathVariable int id, @RequestBody NuovoProdotto dati) {
        return new ProdottoDTO(prodottoService.aggiornaProdotto(id, dati));
    }

    // Eliminazione di un prodotto dal catalogo
    @DeleteMapping("/{id}")
    public ResponseMessage eliminaProdotto(@PathVariable int id) {
        prodottoService.eliminaProdotto(id);
        return new ResponseMessage("Prodotto eliminato");
    }

    // Prodotti eliminati (disponibile = false)
    @GetMapping("/eliminati")
    public List<ProdottoDTO> elencaEliminati() {
        return aDTO(prodottoService.elencaEliminati());
    }

    // Rimette in vendita un prodotto eliminato
    @PutMapping("/{id}/ripristina")
    public ProdottoDTO ripristinaProdotto(@PathVariable int id) {
        return new ProdottoDTO(prodottoService.ripristinaProdotto(id));
    }

    // Converte una lista di entita' Prodotto nei rispettivi DTO
    private List<ProdottoDTO> aDTO(List<Prodotto> prodotti) {
        return prodotti.stream().map(ProdottoDTO::new).collect(Collectors.toList());
    }
}
