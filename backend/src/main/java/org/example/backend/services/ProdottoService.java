package org.example.backend.services;

import org.example.backend.dto.input.NuovoProdotto;
import org.example.backend.entities.Categoria;
import org.example.backend.entities.Prodotto;
import org.example.backend.repositories.DettagliCarrelloRepository;
import org.example.backend.repositories.ProdottoRepository;
import org.example.backend.eccezioni.OperazioneNonConsentitaException;
import org.example.backend.eccezioni.ProdottoNonTrovatoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProdottoService {

    private static final Set<String> ORDINAMENTI_AMMESSI = Set.of("nome", "prezzo", "marca");
    private static final String ORDINAMENTO_DEFAULT = "nome";

    private final ProdottoRepository prodottoRepository;
    private final DettagliCarrelloRepository dettagliCarrelloRepository;

    public ProdottoService(ProdottoRepository prodottoRepository,
                           DettagliCarrelloRepository dettagliCarrelloRepository) {
        this.prodottoRepository = prodottoRepository;
        this.dettagliCarrelloRepository = dettagliCarrelloRepository;
    }

    /* Inserisce un nuovo prodotto */
    @Transactional
    public Prodotto aggiungiProdotto(NuovoProdotto dati) {
        validaDati(dati);

        Prodotto prodotto = new Prodotto();
        prodotto.setNome(dati.getNome());
        prodotto.setCategoria(dati.getCategoria());
        prodotto.setMarca(dati.getMarca());
        prodotto.setPrezzo(dati.getPrezzo());
        prodotto.setQuantita(dati.getQuantita());
        prodotto.setImmagineUrl(dati.getImmagineUrl());
        prodotto.setDettagli(dati.getDettagli());
        prodotto.setDisponibile(true);
        return prodottoRepository.save(prodotto);
    }

    /* Aggiorna un prodotto esistente */
    @Transactional
    public Prodotto aggiornaProdotto(int id, NuovoProdotto dati) {
        validaDati(dati);

        Prodotto esistente = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException(
                        "Prodotto con id " + id + " non trovato"));

        esistente.setNome(dati.getNome());
        esistente.setMarca(dati.getMarca());
        esistente.setPrezzo(dati.getPrezzo());
        esistente.setImmagineUrl(dati.getImmagineUrl());
        esistente.setCategoria(dati.getCategoria());
        esistente.setDettagli(dati.getDettagli());

        return prodottoRepository.save(esistente);
    }

    /* Controlli di validita' comuni a inserimento e modifica */
    private void validaDati(NuovoProdotto dati) {
        if (dati.getNome() == null || dati.getNome().isBlank()) {
            throw new OperazioneNonConsentitaException("Il nome e' obbligatorio");
        }
        if (dati.getMarca() == null || dati.getMarca().isBlank()) {
            throw new OperazioneNonConsentitaException("La marca e' obbligatoria");
        }
        if (dati.getCategoria() == null) {
            throw new OperazioneNonConsentitaException("La categoria e' obbligatoria");
        }
        if (dati.getPrezzo() < 0) {
            throw new OperazioneNonConsentitaException("Il prezzo non puo' essere negativo");
        }
        if (dati.getQuantita() < 0) {
            throw new OperazioneNonConsentitaException("La quantita' non puo' essere negativa");
        }
    }

    /* "Elimina" un prodotto, resta nel database, ma il prodotto diventa non disponibile e non compare piu' nel catalogo */
    @Transactional
    public void eliminaProdotto(int id) {
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato"));
        dettagliCarrelloRepository.deleteByProdotto_Id(id);
        prodotto.setDisponibile(false);
        prodottoRepository.save(prodotto);
    }

    /* Rimette in vendita un prodotto eliminato */
    @Transactional
    public Prodotto ripristinaProdotto(int id) {
        Prodotto prodotto = prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato"));
        prodotto.setDisponibile(true);
        return prodottoRepository.save(prodotto);
    }

    /* Prodotti eliminati */
    public List<Prodotto> elencaEliminati() {
        return prodottoRepository.findByDisponibileFalse();
    }

    /* Trova un prodotto per id o lancia un'eccezione se non esiste. */
    public Prodotto trovaPerId(int id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato"));
    }

    /* Elenca tutti i prodotti in vendita (disponibile = true), senza paginazione  */
    public List<Prodotto> elencaTutti() {
        return prodottoRepository.findByDisponibileTrue();
    }

    /* Ricerca paginata e ordinata nel catalogo, validando i parametri di prezzo. */
    public Page<Prodotto> cercaCatalogoPaginato(String nome, String marca, String categoria,
                                                Double prezzoMin, Double prezzoMax, int pagina, int dimensione,
                                                String ordinaPer, String direzione) {
        if (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax) {
            throw new OperazioneNonConsentitaException("Il prezzo minimo non puo' essere maggiore del massimo");
        }

        Pageable pageable = PageRequest.of(pagina, dimensione, costruisciOrdinamento(ordinaPer, direzione));

        return prodottoRepository.cercaCatalogo(normalizza(nome), normalizza(marca), categoriaOppureNull(categoria),
                prezzoMin, prezzoMax, pageable);
    }

    /* Costruisce l'ordinamento ammesso */
    private Sort costruisciOrdinamento(String ordinaPer, String direzione) {
        String campo = (ordinaPer != null && ORDINAMENTI_AMMESSI.contains(ordinaPer)) ? ordinaPer : ORDINAMENTO_DEFAULT;
        Sort.Direction direzioneSort = "desc".equalsIgnoreCase(direzione) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direzioneSort, campo);
    }

    /* Ripulisce una stringa di ricerca: trim e null se vuota. */
    private String normalizza(String testo) {
        if (testo == null) {
            return null;
        }
        String pulito = testo.trim();
        return pulito.isEmpty() ? null : pulito;
    }

    /* Converte il testo in Categoria, oppure null se vuoto */
    private Categoria categoriaOppureNull(String categoria) {
        String pulito = normalizza(categoria);
        if (pulito == null) {
            return null;
        }
        return Categoria.daTesto(pulito);
    }

}
