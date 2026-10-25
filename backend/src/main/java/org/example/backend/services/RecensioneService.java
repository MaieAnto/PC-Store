package org.example.backend.services;

import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Recensione;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.DettagliOrdineRepository;
import org.example.backend.repositories.ProdottoRepository;
import org.example.backend.repositories.RecensioneRepository;
import org.example.backend.repositories.UtenteRepository;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.example.backend.eccezioni.RecensioneGiaEsistenteException;
import org.example.backend.eccezioni.RecensioneNonConsentitaException;
import org.example.backend.eccezioni.RecensioneNonTrovataException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;
    private final DettagliOrdineRepository dettagliOrdineRepository;

    public RecensioneService(RecensioneRepository recensioneRepository, UtenteRepository utenteRepository,
                             ProdottoRepository prodottoRepository, DettagliOrdineRepository dettagliOrdineRepository) {
        this.recensioneRepository = recensioneRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
        this.dettagliOrdineRepository = dettagliOrdineRepository;
    }

    /* Aggiunge una recensione: solo se l'utente ha acquistato il prodotto e non l'ha gia' recensito. */
    @Transactional
    public Recensione aggiungiRecensione(int idAutore, int idProdotto, int valutazione, String commento) {
        if (valutazione < 1 || valutazione > 5) {
            throw new OperazioneNonConsentitaException("La valutazione deve essere compresa tra 1 e 5");
        }

        Utente autore = utenteRepository.findById(idAutore)
                .orElseThrow(() -> new UtenteNonTrovatoException("Utente con id " + idAutore + " non trovato"));
        Prodotto prodotto = prodottoRepository.findById(idProdotto)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + idProdotto + " non trovato"));

        if (!dettagliOrdineRepository.existsByOrdine_Acquirente_IdAndProdotto_Id(idAutore, idProdotto)) {
            throw new RecensioneNonConsentitaException();
        }

        if (recensioneRepository.existsByAutoreAndProdotto(autore, prodotto)) {
            throw new RecensioneGiaEsistenteException();
        }

        Recensione recensione = new Recensione();
        recensione.setAutore(autore);
        recensione.setProdotto(prodotto);
        recensione.setValutazione(valutazione);
        recensione.setCommento(commento);
        return recensioneRepository.save(recensione);
    }

    /* Modifica una recensione esistente. Consentito solo all'autore della recensione. */
    @Transactional
    public Recensione aggiornaRecensione(int idAutore, int idRecensione, int valutazione, String commento) {
        if (valutazione < 1 || valutazione > 5) {
            throw new OperazioneNonConsentitaException("La valutazione deve essere compresa tra 1 e 5");
        }

        Recensione recensione = recensioneRepository.findById(idRecensione)
                .orElseThrow(() -> new RecensioneNonTrovataException(
                        "Recensione con id " + idRecensione + " non trovata"));

        if (recensione.getAutore().getId() != idAutore) {
            throw new RecensioneNonConsentitaException("Puoi modificare solo le tue recensioni");
        }

        recensione.setValutazione(valutazione);
        recensione.setCommento(commento);
        return recensioneRepository.save(recensione);
    }

    /* Elimina una recensione. Consentito solo all'autore della recensione. */
    @Transactional
    public void eliminaRecensione(int idAutore, int idRecensione) {
        Recensione recensione = recensioneRepository.findById(idRecensione)
                .orElseThrow(() -> new RecensioneNonTrovataException(
                        "Recensione con id " + idRecensione + " non trovata"));

        if (recensione.getAutore().getId() != idAutore) {
            throw new RecensioneNonConsentitaException("Puoi eliminare solo le tue recensioni");
        }

        recensioneRepository.delete(recensione);
    }

    /* Recensioni di un prodotto, dalla piu' recente alla piu' vecchia. */
    public List<Recensione> recensioniProdotto(int idProdotto) {
        return recensioneRepository.findByProdotto_IdOrderByDataRecensioneDesc(idProdotto);
    }

    /* Media delle valutazioni di un prodotto */
    public double mediaValutazioni(int idProdotto) {
        List<Recensione> recensioni = recensioneRepository.findByProdotto_IdOrderByDataRecensioneDesc(idProdotto);
        if (recensioni.isEmpty()) {
            return 0.0;
        }
        int somma = 0;
        for (Recensione recensione : recensioni) {
            somma += recensione.getValutazione();
        }
        return (double) somma / recensioni.size();
    }
}
