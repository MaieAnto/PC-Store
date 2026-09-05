package org.example.backend.services;

import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.CarrelloRepository;
import org.example.backend.repositories.ProdottoRepository;
import org.example.backend.repositories.UtenteRepository;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarrelloService {

    private final CarrelloRepository carrelloRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    public CarrelloService(CarrelloRepository carrelloRepository, UtenteRepository utenteRepository,
                           ProdottoRepository prodottoRepository) {
        this.carrelloRepository = carrelloRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    /* Restituisce il carrello dell'utente, se non esiste lo crea */
    @Transactional
    public Carrello getOppureCreaCarrello(int idUtente) {
        return carrelloRepository.findByUtente_Id(idUtente).orElseGet(() -> {
            Utente utente = utenteRepository.findById(idUtente)
                    .orElseThrow(() -> new UtenteNonTrovatoException("Utente con id " + idUtente + " non trovato"));
            Carrello carrello = new Carrello();
            carrello.setUtente(utente);
            carrello.setDettagli(new ArrayList<>());
            return carrelloRepository.save(carrello);
        });
    }

    /* Aggiunge un prodotto al carrello. Se il prodotto e' gia' presente, ne incrementa la quantita' */
    @Transactional
    public Carrello aggiungiAlCarrello(int idUtente, int idProdotto, int quantita) {
        if (quantita <= 0) {
            throw new OperazioneNonConsentitaException("La quantita' deve essere maggiore di zero");
        }
        Carrello carrello = getOppureCreaCarrello(idUtente);
        Prodotto prodotto = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + idProdotto + " non trovato"));

        DettagliCarrello esistente = trovaRiga(carrello, idProdotto);
        if (esistente != null) {
            esistente.setQuantita(esistente.getQuantita() + quantita);
        } else {
            DettagliCarrello riga = new DettagliCarrello();
            riga.setCarrello(carrello);
            riga.setProdotto(prodotto);
            riga.setQuantita(quantita);
            carrello.getDettagli().add(riga);
        }
        return carrelloRepository.save(carrello);
    }

    /* Imposta una quantita' precisa per un prodotto gia' presente nel carrello. */
    @Transactional
    public Carrello aggiornaQuantita(int idUtente, int idProdotto, int nuovaQuantita) {
        if (nuovaQuantita <= 0) {
            throw new OperazioneNonConsentitaException("La quantita' deve essere maggiore di zero");
        }
        Carrello carrello = getOppureCreaCarrello(idUtente);
        DettagliCarrello riga = trovaRiga(carrello, idProdotto);
        if (riga == null) {
            throw new ProdottoNonTrovatoException("Il prodotto con id " + idProdotto + " non e' presente nel carrello");
        }
        riga.setQuantita(nuovaQuantita);
        return carrelloRepository.save(carrello);
    }

    /* Rimuove un prodotto dal carrello */
    @Transactional
    public Carrello rimuoviDalCarrello(int idUtente, int idProdotto) {
        Carrello carrello = getOppureCreaCarrello(idUtente);
        DettagliCarrello riga = trovaRiga(carrello, idProdotto);
        if (riga == null) {
            throw new ProdottoNonTrovatoException("Il prodotto con id " + idProdotto + " non e' presente nel carrello");
        }
        carrello.getDettagli().remove(riga);
        return carrelloRepository.save(carrello);
    }

    /* Svuota completamente il carrello dell'utente. */
    @Transactional
    public Carrello svuotaCarrello(int idUtente) {
        Carrello carrello = getOppureCreaCarrello(idUtente);
        carrello.getDettagli().clear();
        return carrelloRepository.save(carrello);
    }

    /* Restituisce le righe del carrello dell'utente; lista vuota se il carrello
       non esiste ancora (metodo in sola lettura: non crea il carrello). */
    public List<DettagliCarrello> getDettagli(int idUtente) {
        return carrelloRepository.findByUtente_Id(idUtente)
                .map(carrello -> new ArrayList<>(carrello.getDettagli()))
                .orElseGet(ArrayList::new);
    }

    /* Cerca la riga del carrello relativa a un prodotto; null se assente. */
    private DettagliCarrello trovaRiga(Carrello carrello, int idProdotto) {
        for (DettagliCarrello riga : carrello.getDettagli()) {
            if (riga.getProdotto().getId() == idProdotto) {
                return riga;
            }
        }
        return null;
    }
}
