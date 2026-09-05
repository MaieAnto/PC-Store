package org.example.backend.services;

import org.example.backend.entities.Prodotto;
import org.example.backend.repositories.ProdottoRepository;
import org.example.backend.eccezioni.CodiceProdottoGiaEsistenteException;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    /* Inserisce un nuovo prodotto */
    @Transactional
    public Prodotto aggiungiProdotto(Prodotto prodotto) {
        if (prodotto.getCodice() != null && prodottoRepository.existsByCodice(prodotto.getCodice())) {
            throw new CodiceProdottoGiaEsistenteException();
        }
        return prodottoRepository.save(prodotto);
    }

    /* Aggiorna un prodotto esistente*/
    @Transactional
    public Prodotto aggiornaProdotto(Prodotto prodotto) {
        if (!prodottoRepository.existsById(prodotto.getId())) {
            throw new ProdottoNonTrovatoException("Prodotto con id " + prodotto.getId() + " non trovato");
        }
        return prodottoRepository.save(prodotto);
    }

    @Transactional
    public void eliminaProdotto(int id) {
        if (!prodottoRepository.existsById(id)) {
            throw new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato");
        }
        prodottoRepository.deleteById(id);
    }

    public Prodotto trovaPerId(int id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato"));
    }

    public Prodotto trovaPerCodice(String codice) {
        return prodottoRepository.findByCodice(codice)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con codice " + codice + " non trovato"));
    }

    public List<Prodotto> elencaTutti() {
        return prodottoRepository.findAll();
    }

    public List<Prodotto> cercaPerNome(String nome) {
        return prodottoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Prodotto> cercaPerMarca(String marca) {
        return prodottoRepository.findByMarcaIgnoreCase(marca);
    }

    /* Ricerca per fascia di prezzo*/
    public List<Prodotto> cercaPerFasciaPrezzo(float prezzoMin, float prezzoMax) {
        if (prezzoMin > prezzoMax) {
            throw new OperazioneNonConsentitaException("Il prezzo minimo non puo' essere maggiore del massimo");
        }
        return prodottoRepository.findByPrezzoBetween(prezzoMin, prezzoMax);
    }

    /* Restituisce solo i prodotti con almeno un'unita' disponibile nel magazzino */
    public List<Prodotto> disponibili() {
        return prodottoRepository.findByQuantitaGreaterThan(0);
    }
}
