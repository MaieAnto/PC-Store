package org.example.backend.services;

import org.example.backend.dto.RifornimentoRequest;
import org.example.backend.dto.RigaRifornimentoRequest;
import org.example.backend.entities.DettagliRifornimento;
import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Rifornimento;
import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.ProdottoRepository;
import org.example.backend.repositories.RifornimentoRepository;
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
public class RifornimentoService {

    private final RifornimentoRepository rifornimentoRepository;
    private final UtenteRepository utenteRepository;
    private final ProdottoRepository prodottoRepository;

    public RifornimentoService(RifornimentoRepository rifornimentoRepository, UtenteRepository utenteRepository,
                               ProdottoRepository prodottoRepository) {
        this.rifornimentoRepository = rifornimentoRepository;
        this.utenteRepository = utenteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    /* Registra un rifornimento e aumenta le scorte dei prodotti riforniti*/
    @Transactional
    public Rifornimento creaRifornimento(int idFornitore, RifornimentoRequest richiesta) {
        Utente fornitore = utenteRepository.findById(idFornitore)
                .orElseThrow(() -> new UtenteNonTrovatoException("Utente con id " + idFornitore + " non trovato"));

        if (fornitore.getRuolo() != Ruolo.FORNITORE) {
            throw new OperazioneNonConsentitaException("Solo un fornitore puo' rifornire il magazzino");
        }

        if (richiesta == null || richiesta.getRighe() == null || richiesta.getRighe().isEmpty()) {
            throw new OperazioneNonConsentitaException("Il rifornimento non contiene prodotti");
        }

        Rifornimento rifornimento = new Rifornimento();
        rifornimento.setFornitore(fornitore);
        List<DettagliRifornimento> dettagli = new ArrayList<>();

        for (RigaRifornimentoRequest riga : richiesta.getRighe()) {
            if (riga.getQuantita() <= 0) {
                throw new OperazioneNonConsentitaException("La quantita' di rifornimento deve essere maggiore di zero");
            }

            if (riga.getCostoUnitario() < 0) {
                throw new OperazioneNonConsentitaException("Il costo unitario non puo' essere negativo");
            }

            Prodotto prodotto = prodottoRepository.findById(riga.getIdProdotto())
                    .orElseThrow(() -> new ProdottoNonTrovatoException(
                            "Prodotto con id " + riga.getIdProdotto() + " non trovato"));

            if (!prodotto.isDisponibile()) {
                throw new OperazioneNonConsentitaException("Il prodotto " + prodotto.getNome() + " non e' piu' in vendita");
            }

            DettagliRifornimento dettaglio = new DettagliRifornimento();
            dettaglio.setRifornimento(rifornimento);
            dettaglio.setProdotto(prodotto);
            dettaglio.setQuantita(riga.getQuantita());
            dettaglio.setCostoUnitario(riga.getCostoUnitario());
            dettagli.add(dettaglio);
            prodotto.setQuantita(prodotto.getQuantita() + riga.getQuantita());
        }

        rifornimento.setDettagli(dettagli);
        return rifornimentoRepository.save(rifornimento);
    }

    /* Storico dei rifornimenti di un fornitore dal piu' recente al piu' vecchio. */
    public List<Rifornimento> storicoRifornimenti(int idFornitore) {
        return rifornimentoRepository.findByFornitore_IdOrderByDataRifornimentoDesc(idFornitore);
    }
}
