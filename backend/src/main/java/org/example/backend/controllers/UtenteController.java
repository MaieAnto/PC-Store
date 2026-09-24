package org.example.backend.controllers;

import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import org.example.backend.eccezioni.CredenzialiNonValideException;
import org.example.backend.eccezioni.EmailGiaRegistrataException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.example.backend.services.UtenteService;
import org.example.backend.support.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    /* Registrazione di un nuovo utente (201 se creato, 409 se l'email esiste gia'). */
    @PostMapping("/registrazione")
    public ResponseEntity<?> registra(@RequestBody Utente utente) {
        try {
            return new ResponseEntity<>(utenteService.registra(utente), HttpStatus.CREATED);
        } catch (EmailGiaRegistrataException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    /* Autenticazione: verifica email e password e restituisce l'utente (401 se errate). */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            return new ResponseEntity<>(utenteService.login(email, password), HttpStatus.OK);
        } catch (CredenzialiNonValideException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    /* Dettaglio di un utente per id. */
    @GetMapping("/{id}")
    public ResponseEntity<?> trovaPerId(@PathVariable int id) {
        try {
            return new ResponseEntity<>(utenteService.trovaPerId(id), HttpStatus.OK);
        } catch (UtenteNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /* Ricerca di un utente per email. */
    @GetMapping("/per-email")
    public ResponseEntity<?> trovaPerEmail(@RequestParam String email) {
        try {
            return new ResponseEntity<>(utenteService.trovaPerEmail(email), HttpStatus.OK);
        } catch (UtenteNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /* Elenco degli utenti per ruolo. */
    @GetMapping("/ruolo/{ruolo}")
    public ResponseEntity<List<Utente>> trovaPerRuolo(@PathVariable Ruolo ruolo) {
        return new ResponseEntity<>(utenteService.trovaPerRuolo(ruolo), HttpStatus.OK);
    }

    /* Elenco completo degli utenti. */
    @GetMapping
    public ResponseEntity<List<Utente>> elencaTutti() {
        return new ResponseEntity<>(utenteService.elencaTutti(), HttpStatus.OK);
    }
}
