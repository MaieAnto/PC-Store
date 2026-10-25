package org.example.backend.dto.input;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/* Corpo della richiesta di rifornimento: l'elenco delle righe da rifornire. */
@Getter
@Setter
public class RifornimentoRequest {

    private List<RigaRifornimentoRequest> righe;
}
