package org.example.backend.dto.input;

import org.example.backend.entities.Categoria;
import lombok.Getter;
import lombok.Setter;

/* Corpo della richiesta di inserimento di un nuovo prodotto */
@Getter
@Setter
public class NuovoProdotto {

    private String nome;
    private Categoria categoria;
    private String marca;
    private double prezzo;
    private int quantita;
    private String immagineUrl;
    private String dettagli;
}
