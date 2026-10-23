package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecensioneRequest {
    private int idProdotto;
    private int valutazione;
    private String commento;
}
