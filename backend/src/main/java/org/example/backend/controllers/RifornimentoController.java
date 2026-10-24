package org.example.backend.controllers;

import org.example.backend.dto.RifornimentoRequest;
import org.example.backend.entities.Rifornimento;
import org.example.backend.services.RifornimentoService;
import org.example.backend.services.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rifornimenti")
public class RifornimentoController {

    private final RifornimentoService rifornimentoService;
    private final UtenteService utenteService;

    public RifornimentoController(RifornimentoService rifornimentoService, UtenteService utenteService) {
        this.rifornimentoService = rifornimentoService;
        this.utenteService = utenteService;
    }

    private int idUtenteLoggato(Authentication auth) {
        return utenteService.trovaPerEmail(auth.getName()).getId();
    }

    // Registra un rifornimento e aumenta le scorte dei prodotti riforniti
    @PostMapping
    public ResponseEntity<Rifornimento> creaRifornimento(Authentication auth, @RequestBody RifornimentoRequest richiesta) {
        return new ResponseEntity<>(rifornimentoService.creaRifornimento(idUtenteLoggato(auth), richiesta), HttpStatus.CREATED);
    }

    // Storico rifornimenti del fornitore corrente, dal piu' recente al piu' vecchio
    @GetMapping("/fornitore")
    public List<Rifornimento> storicoRifornimenti(Authentication auth) {
        return rifornimentoService.storicoRifornimenti(idUtenteLoggato(auth));
    }
}
