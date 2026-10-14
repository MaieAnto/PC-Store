package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Prodotto;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginaProdotti {

    private List<Prodotto> content;
    private int pagina;
    private int dimensione;
    private long totaleElementi;
    private int totalePagine;

    public PaginaProdotti() {
    }

    public PaginaProdotti(Page<Prodotto> page) {
        this.content = page.getContent();
        this.pagina = page.getNumber();
        this.dimensione = page.getSize();
        this.totaleElementi = page.getTotalElements();
        this.totalePagine = page.getTotalPages();
    }
}
