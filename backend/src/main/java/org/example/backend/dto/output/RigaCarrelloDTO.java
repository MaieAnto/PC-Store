package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.DettagliCarrello;
import org.example.backend.entities.Prodotto;

/* Una riga del carrello */
@Getter
@Setter
public class RigaCarrelloDTO {

    private int id;
    private int idProdotto;
    private String nome;
    private String marca;
    private String categoria;
    private String immagineUrl;
    private double prezzo;
    private int quantita;              // quantita' richiesta nel carrello
    private int quantitaDisponibile;   // scorte attuali del prodotto
    private double subtotale;

    public RigaCarrelloDTO(DettagliCarrello riga) {
        Prodotto prodotto = riga.getProdotto();
        this.id = riga.getId();
        this.idProdotto = prodotto.getId();
        this.nome = prodotto.getNome();
        this.marca = prodotto.getMarca();
        this.categoria = prodotto.getCategoria() != null ? prodotto.getCategoria().name() : null;
        this.immagineUrl = prodotto.getImmagineUrl();
        this.prezzo = prodotto.getPrezzo();
        this.quantita = riga.getQuantita();
        this.quantitaDisponibile = prodotto.getQuantita();
        this.subtotale = prodotto.getPrezzo() * riga.getQuantita();
    }
}
