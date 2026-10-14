package org.example.backend.services;

import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.entities.DettagliOrdine;
import org.example.backend.entities.Ordine;
import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.CarrelloRepository;
import org.example.backend.repositories.OrdineRepository;
import org.example.backend.repositories.UtenteRepository;
import org.example.backend.eccezioni.CarrelloVuotoException;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.OrdineNonTrovatoException;
import org.example.backend.eccezioni.QuantitaNonDisponibileException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final CarrelloRepository carrelloRepository;
    private final UtenteRepository utenteRepository;

    public OrdineService(OrdineRepository ordineRepository, CarrelloRepository carrelloRepository,
                         UtenteRepository utenteRepository) {
        this.ordineRepository = ordineRepository;
        this.carrelloRepository = carrelloRepository;
        this.utenteRepository = utenteRepository;
    }

    /* Crea un ordine a partire dal carrello del cliente */
    @Transactional
    public Ordine creaOrdineDaCarrello(int idUtente) {
        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new UtenteNonTrovatoException("Utente con id " + idUtente + " non trovato"));

        if (utente.getRuolo() != Ruolo.CLIENTE) {
            throw new OperazioneNonConsentitaException("Solo un cliente puo' effettuare ordini");
        }

        Carrello carrello = carrelloRepository.findByUtente_Id(idUtente).orElse(null);
        if (carrello == null || carrello.getDettagli() == null || carrello.getDettagli().isEmpty()) {
            throw new CarrelloVuotoException();
        }

        Ordine ordine = new Ordine();
        ordine.setAcquirente(utente);
        List<DettagliOrdine> righeOrdine = new ArrayList<>();

        for (DettagliCarrello riga : carrello.getDettagli()) {
            Prodotto prodotto = riga.getProdotto();
            int richiesta = riga.getQuantita();
            if (prodotto.getQuantita() < richiesta) {
                throw new QuantitaNonDisponibileException(
                        "Scorta insufficiente per il prodotto: " + prodotto.getNome());
            }

            DettagliOrdine rigaOrdine = new DettagliOrdine();
            rigaOrdine.setOrdine(ordine);
            rigaOrdine.setProdotto(prodotto);
            rigaOrdine.setQuantita(richiesta);
            rigaOrdine.setPrezzoUnitario(prodotto.getPrezzo());
            righeOrdine.add(rigaOrdine);
            prodotto.setQuantita(prodotto.getQuantita() - richiesta);
        }

        ordine.setDettagli(righeOrdine);
        Ordine ordineSalvato = ordineRepository.save(ordine);
        carrello.getDettagli().clear();
        carrelloRepository.save(carrello);

        return ordineSalvato;
    }

    public Ordine trovaPerId(int id) {
        return ordineRepository.findById(id)
                .orElseThrow(() -> new OrdineNonTrovatoException("Ordine con id " + id + " non trovato"));
    }

    /* Storico degli ordini di un cliente, dal piu' recente al piu' vecchio. */
    public List<Ordine> storicoOrdini(int idUtente) {
        return ordineRepository.findByAcquirente_IdOrderByDataOrdineDesc(idUtente);
    }
}
