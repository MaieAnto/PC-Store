package org.example.backend.dto.input;

import lombok.Getter;
import lombok.Setter;

/* Corpo della richiesta per aggiungere/aggiornare un prodotto nel carrello. */
@Getter
@Setter
public class RigaCarrelloRequest {

    private int idProdotto;
    private int quantita;
}
