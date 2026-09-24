package org.example.backend.controllers;

import org.example.backend.entities.Prodotto;
import org.example.backend.eccezioni.CodiceProdottoGiaEsistenteException;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
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

    /* Catalogo: tutti i prodotti in vendita (disponibile = true). */
    @GetMapping
    public ResponseEntity<List<Prodotto>> elencaTutti() {
        return new ResponseEntity<>(prodottoService.elencaTutti(), HttpStatus.OK);
    }

    /* Ricerca per nome (contiene, case-insensitive). */
    @GetMapping("/cerca")
    public ResponseEntity<List<Prodotto>> cercaPerNome(@RequestParam String nome) {
        return new ResponseEntity<>(prodottoService.cercaPerNome(nome), HttpStatus.OK);
    }

    /* Ricerca per marca. */
    @GetMapping("/marca")
    public ResponseEntity<List<Prodotto>> cercaPerMarca(@RequestParam String marca) {
        return new ResponseEntity<>(prodottoService.cercaPerMarca(marca), HttpStatus.OK);
    }

    /* Ricerca per fascia di prezzo. */
    @GetMapping("/prezzo")
    public ResponseEntity<?> cercaPerFasciaPrezzo(@RequestParam float min, @RequestParam float max) {
        try {
            return new ResponseEntity<>(prodottoService.cercaPerFasciaPrezzo(min, max), HttpStatus.OK);
        } catch (OperazioneNonConsentitaException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Dettaglio di un singolo prodotto. */
    @GetMapping("/{id}")
    public ResponseEntity<?> trovaPerId(@PathVariable int id) {
        try {
            return new ResponseEntity<>(prodottoService.trovaPerId(id), HttpStatus.OK);
        } catch (ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /* Ricerca per codice prodotto. */
    @GetMapping("/codice/{codice}")
    public ResponseEntity<?> trovaPerCodice(@PathVariable String codice) {
        try {
            return new ResponseEntity<>(prodottoService.trovaPerCodice(codice), HttpStatus.OK);
        } catch (ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /* Inserimento di un nuovo prodotto.
       Il corpo (JSON) contiene i campi comuni + categoria + dettagli. */
    @PostMapping
    public ResponseEntity<?> aggiungi(@RequestBody Prodotto prodotto) {
        try {
            Prodotto creato = prodottoService.aggiungiProdotto(prodotto);
            return new ResponseEntity<>(creato, HttpStatus.CREATED);
        } catch (CodiceProdottoGiaEsistenteException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Modifica di un prodotto esistente. L'id viene dal percorso e prevale su
       quello eventualmente presente nel corpo. */
    @PutMapping("/{id}")
    public ResponseEntity<?> aggiorna(@PathVariable int id, @RequestBody Prodotto prodotto) {
        prodotto.setId(id);
        try {
            return new ResponseEntity<>(prodottoService.aggiornaProdotto(prodotto), HttpStatus.OK);
        } catch (ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (CodiceProdottoGiaEsistenteException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Eliminazione (logica) di un prodotto: sparisce dal catalogo ma resta nello storico. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminaProdotto(@PathVariable int id) {
        try {
            prodottoService.eliminaProdotto(id);
            return new ResponseEntity<>(new ResponseMessage("Prodotto eliminato"), HttpStatus.OK);
        } catch (ProdottoNonTrovatoException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
