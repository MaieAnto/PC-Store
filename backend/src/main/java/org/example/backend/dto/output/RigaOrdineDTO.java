package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.DettagliOrdine;
import org.example.backend.entities.Prodotto;

/* Riga di un ordine: dati del prodotto al momento dell'acquisto e subtotale */
@Getter
@Setter
public class RigaOrdineDTO {

    private int id;
    private int idProdotto;
    private String nome;
    private String marca;
    private String immagineUrl;
    private int quantita;
    private double prezzoUnitario;
    private double subtotale;

    public RigaOrdineDTO(DettagliOrdine riga) {
        Prodotto prodotto = riga.getProdotto();
        this.id = riga.getId();
        this.idProdotto = prodotto.getId();
        this.nome = prodotto.getNome();
        this.marca = prodotto.getMarca();
        this.immagineUrl = prodotto.getImmagineUrl();
        this.quantita = riga.getQuantita();
        this.prezzoUnitario = riga.getPrezzoUnitario();
        this.subtotale = riga.getPrezzoUnitario() * riga.getQuantita();
    }
}
