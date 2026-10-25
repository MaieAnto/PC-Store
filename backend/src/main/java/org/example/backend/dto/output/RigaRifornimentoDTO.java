package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.DettagliRifornimento;
import org.example.backend.entities.Prodotto;

/* Riga di un rifornimento */
@Getter
@Setter
public class RigaRifornimentoDTO {

    private int id;
    private int idProdotto;
    private String nome;
    private String marca;
    private int quantita;
    private double costoUnitario;
    private double subtotale;

    public RigaRifornimentoDTO(DettagliRifornimento riga) {
        Prodotto prodotto = riga.getProdotto();
        this.id = riga.getId();
        this.idProdotto = prodotto.getId();
        this.nome = prodotto.getNome();
        this.marca = prodotto.getMarca();
        this.quantita = riga.getQuantita();
        this.costoUnitario = riga.getCostoUnitario();
        this.subtotale = riga.getCostoUnitario() * riga.getQuantita();
    }
}
