package org.example.backend.controllers;

import org.example.backend.dto.RifornimentoRequest;
import org.example.backend.entities.Rifornimento;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.example.backend.services.RifornimentoService;
import org.example.backend.support.ResponseMessage;
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

    /* Registra un rifornimento e aumenta le scorte dei prodotti riforniti. */
    @PostMapping("/{idFornitore}")
    public ResponseEntity<?> creaRifornimento(@PathVariable int idFornitore,
                                              @RequestBody RifornimentoRequest richiesta) {
        try {
            Rifornimento salvato = rifornimentoService.creaRifornimento(idFornitore, richiesta);
            return new ResponseEntity<>(salvato, HttpStatus.CREATED);
        } catch (UtenteNonTrovatoException | ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /* Storico rifornimenti di un fornitore, dal piu' recente al piu' vecchio. */
    @GetMapping("/fornitore/{idFornitore}")
    public ResponseEntity<List<Rifornimento>> storicoRifornimenti(@PathVariable int idFornitore) {
        return new ResponseEntity<>(rifornimentoService.storicoRifornimenti(idFornitore), HttpStatus.OK);
    }
}
