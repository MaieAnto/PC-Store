package org.example.backend.controllers;

import org.example.backend.dto.RifornimentoRequest;
import org.example.backend.entities.Rifornimento;
import org.example.backend.services.RifornimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rifornimenti")
public class RifornimentoController {

    private final RifornimentoService rifornimentoService;

    public RifornimentoController(RifornimentoService rifornimentoService) {
        this.rifornimentoService = rifornimentoService;
    }

    // Registra un rifornimento e aumenta le scorte dei prodotti riforniti
    @PostMapping("/{idFornitore}")
    public ResponseEntity<Rifornimento> creaRifornimento(@PathVariable int idFornitore, @RequestBody RifornimentoRequest richiesta) {
        return new ResponseEntity<>(rifornimentoService.creaRifornimento(idFornitore, richiesta), HttpStatus.CREATED);
    }

    // Storico rifornimenti di un fornitore, dal piu' recente al piu' vecchio
    @GetMapping("/fornitore/{idFornitore}")
    public List<Rifornimento> storicoRifornimenti(@PathVariable int idFornitore) {
        return rifornimentoService.storicoRifornimenti(idFornitore);
    }
}
