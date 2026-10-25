package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Prodotto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/* Pagina di prodotti del catalogo */
@Getter
@Setter
public class PaginaProdotti {

    private List<ProdottoDTO> content;
    private int pagina;
    private int dimensione;
    private long totaleElementi;
    private int totalePagine;

    public PaginaProdotti() {
    }

    public PaginaProdotti(Page<Prodotto> page) {
        this.content = page.getContent().stream().map(ProdottoDTO::new).collect(Collectors.toList());
        this.pagina = page.getNumber();
        this.dimensione = page.getSize();
        this.totaleElementi = page.getTotalElements();
        this.totalePagine = page.getTotalPages();
    }
}
