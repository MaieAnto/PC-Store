package org.example.backend.controllers;

import org.example.backend.dto.output.OrdineDTO;
import org.example.backend.services.OrdineService;
import org.example.backend.support.UtenteCorrente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {

    private final OrdineService ordineService;
    private final UtenteCorrente utenteCorrente;

    public OrdineController(OrdineService ordineService, UtenteCorrente utenteCorrente) {
        this.ordineService = ordineService;
        this.utenteCorrente = utenteCorrente;
    }

    // Crea un nuovo ordine a partire dal carrello dell'utente corrente
    @PostMapping
    public ResponseEntity<OrdineDTO> creaOrdineDaCarrello(Authentication auth) {
        OrdineDTO ordine = new OrdineDTO(ordineService.creaOrdineDaCarrello(utenteCorrente.id(auth)));
        return new ResponseEntity<>(ordine, HttpStatus.CREATED);
    }

    // Storico degli ordini dell'utente corrente
    @GetMapping("/utente")
    public List<OrdineDTO> storicoOrdini(Authentication auth) {
        return ordineService.storicoOrdini(utenteCorrente.id(auth)).stream()
                .map(OrdineDTO::new)
                .collect(Collectors.toList());
    }
}
