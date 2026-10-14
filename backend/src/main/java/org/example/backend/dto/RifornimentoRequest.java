package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RifornimentoRequest {

    private List<RigaRifornimentoRequest> righe;
}
