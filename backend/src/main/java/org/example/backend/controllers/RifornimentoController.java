package org.example.backend.controllers;

import org.example.backend.dto.output.RifornimentoDTO;
import org.example.backend.dto.input.RifornimentoRequest;
import org.example.backend.services.RifornimentoService;
import org.example.backend.support.UtenteCorrente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rifornimenti")
public class RifornimentoController {

    private final RifornimentoService rifornimentoService;
    private final UtenteCorrente utenteCorrente;

    public RifornimentoController(RifornimentoService rifornimentoService, UtenteCorrente utenteCorrente) {
        this.rifornimentoService = rifornimentoService;
        this.utenteCorrente = utenteCorrente;
    }

    // Registra un rifornimento e aumenta le scorte dei prodotti riforniti
    @PostMapping
    public ResponseEntity<RifornimentoDTO> creaRifornimento(Authentication auth, @RequestBody RifornimentoRequest richiesta) {
        RifornimentoDTO rifornimento = new RifornimentoDTO(rifornimentoService.creaRifornimento(utenteCorrente.id(auth), richiesta));
        return new ResponseEntity<>(rifornimento, HttpStatus.CREATED);
    }

    // Storico rifornimenti del fornitore corrente, dal piu' recente al piu' vecchio
    @GetMapping("/fornitore")
    public List<RifornimentoDTO> storicoRifornimenti(Authentication auth) {
        return rifornimentoService.storicoRifornimenti(utenteCorrente.id(auth)).stream()
                .map(RifornimentoDTO::new)
                .collect(Collectors.toList());
    }
}
