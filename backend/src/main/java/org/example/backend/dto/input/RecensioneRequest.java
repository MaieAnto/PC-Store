package org.example.backend.dto.input;

import lombok.Getter;
import lombok.Setter;

/* Corpo della richiesta di creazione/modifica di una recensione. */
@Getter
@Setter
public class RecensioneRequest {
    private int idProdotto;
    private int valutazione;
    private String commento;
}
