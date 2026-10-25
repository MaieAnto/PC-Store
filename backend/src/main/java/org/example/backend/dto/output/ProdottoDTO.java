package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Categoria;
import org.example.backend.entities.Prodotto;

/* Prodotto restituito al frontend */
@Getter
@Setter
public class ProdottoDTO {

    private int id;
    private String nome;
    private Categoria categoria;
    private String marca;
    private double prezzo;
    private int quantita;
    private String immagineUrl;
    private String dettagli;
    private boolean disponibile;

    public ProdottoDTO(Prodotto prodotto) {
        this.id = prodotto.getId();
        this.nome = prodotto.getNome();
        this.categoria = prodotto.getCategoria();
        this.marca = prodotto.getMarca();
        this.prezzo = prodotto.getPrezzo();
        this.quantita = prodotto.getQuantita();
        this.immagineUrl = prodotto.getImmagineUrl();
        this.dettagli = prodotto.getDettagli();
        this.disponibile = prodotto.isDisponibile();
    }
}
