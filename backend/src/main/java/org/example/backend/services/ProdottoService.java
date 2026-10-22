package org.example.backend.services;

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
    public Prodotto aggiungiProdotto(Prodotto prodotto) {
        prodotto.setId(0);
        prodotto.setDisponibile(true);
        return prodottoRepository.save(prodotto);
    }

    /* Aggiorna un prodotto esistente */
    @Transactional
    public Prodotto aggiornaProdotto(Prodotto datiProdotto) {
        Prodotto esistente = prodottoRepository.findById(datiProdotto.getId())
                .orElseThrow(() -> new ProdottoNonTrovatoException(
                        "Prodotto con id " + datiProdotto.getId() + " non trovato"));

        esistente.setNome(datiProdotto.getNome());
        esistente.setMarca(datiProdotto.getMarca());
        esistente.setPrezzo(datiProdotto.getPrezzo());
        esistente.setQuantita(datiProdotto.getQuantita());
        esistente.setImmagineUrl(datiProdotto.getImmagineUrl());
        esistente.setCategoria(datiProdotto.getCategoria());
        esistente.setDettagli(datiProdotto.getDettagli());

        return prodottoRepository.save(esistente);
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

    public Prodotto trovaPerId(int id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNonTrovatoException("Prodotto con id " + id + " non trovato"));
    }

    /* Catalogo */
    public List<Prodotto> elencaTutti() {
        return prodottoRepository.findByDisponibileTrue();
    }

    public Page<Prodotto> cercaCatalogoPaginato(String nome, String marca, String categoria,
                                                Float prezzoMin, Float prezzoMax, int pagina, int dimensione,
                                                String ordinaPer, String direzione) {
        if (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax) {
            throw new OperazioneNonConsentitaException("Il prezzo minimo non puo' essere maggiore del massimo");
        }

        Pageable pageable = PageRequest.of(pagina, dimensione, costruisciOrdinamento(ordinaPer, direzione));

        return prodottoRepository.cercaCatalogo(normalizza(nome), normalizza(marca), categoriaOppureNull(categoria),
                prezzoMin, prezzoMax, pageable);
    }

    private Sort costruisciOrdinamento(String ordinaPer, String direzione) {
        String campo = (ordinaPer != null && ORDINAMENTI_AMMESSI.contains(ordinaPer)) ? ordinaPer : ORDINAMENTO_DEFAULT;
        Sort.Direction direzioneSort = "desc".equalsIgnoreCase(direzione) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direzioneSort, campo);
    }

    private String normalizza(String testo) {
        if (testo == null) {
            return null;
        }
        String pulito = testo.trim();
        return pulito.isEmpty() ? null : pulito;
    }

    private Categoria categoriaOppureNull(String categoria) {
        String pulito = normalizza(categoria);
        if (pulito == null || "Tutte le categorie".equalsIgnoreCase(pulito)) {
            return null;
        }
        return Categoria.daTesto(pulito);
    }

}
