package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RigaRifornimentoRequest {

    private int idProdotto;
    private int quantita;
    private double costoUnitario;
}
