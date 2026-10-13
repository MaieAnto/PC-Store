package org.example.backend.controllers;

import org.example.backend.dto.CredenzialiLogin;
import org.example.backend.dto.RispostaLogin;
import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import org.example.backend.security.JwtUtil;
import org.example.backend.services.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;
    private final JwtUtil jwtUtil;

    public UtenteController(UtenteService utenteService, JwtUtil jwtUtil) {
        this.utenteService = utenteService;
        this.jwtUtil = jwtUtil;
    }

    // Registrazione
    @PostMapping("/registrazione")
    public ResponseEntity<Utente> registra(@RequestBody Utente utente) {
        return new ResponseEntity<>(utenteService.registra(utente), HttpStatus.CREATED);
    }

    // Login : verifica le credenziali e rilascia un token JWT con email e ruolo insieme ai dati dell'utente
    @PostMapping("/login")
    public RispostaLogin login(@RequestBody CredenzialiLogin credenziali) {
        Utente utente = utenteService.login(credenziali.getEmail(), credenziali.getPassword());
        String token = jwtUtil.generateToken(utente.getEmail(), utente.getRuolo());
        return new RispostaLogin(token, utente);
    }

    // Dettaglio di un utente per id
    @GetMapping("/{id}")
    public Utente trovaPerId(@PathVariable int id) {
        return utenteService.trovaPerId(id);
    }

    // Ricerca di un utente per email
    @GetMapping("/per-email")
    public Utente trovaPerEmail(@RequestParam String email) {
        return utenteService.trovaPerEmail(email);
    }

    // Elenco degli utenti per ruolo
    @GetMapping("/ruolo/{ruolo}")
    public List<Utente> trovaPerRuolo(@PathVariable Ruolo ruolo) {
        return utenteService.trovaPerRuolo(ruolo);
    }

    // Elenco completo degli utenti
    @GetMapping
    public List<Utente> elencaTutti() {
        return utenteService.elencaTutti();
    }
}
