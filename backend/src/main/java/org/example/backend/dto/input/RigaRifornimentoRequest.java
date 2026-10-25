package org.example.backend.dto.input;

import lombok.Getter;
import lombok.Setter;

/* Una riga della richiesta di rifornimento: prodotto, quantita' e costo unitario. */
@Getter
@Setter
public class RigaRifornimentoRequest {

    private int idProdotto;
    private int quantita;
    private double costoUnitario;
}
