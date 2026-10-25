package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.DettagliRifornimento;
import org.example.backend.entities.Rifornimento;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/* Rifornimento restituito al frontend */
@Getter
@Setter
public class RifornimentoDTO {

    private int id;
    private Date dataRifornimento;
    private List<RigaRifornimentoDTO> righe;
    private double totaleCosto;

    // Costruisce il DTO a partire da un rifornimento, calcolando righe e costo totale.
    public RifornimentoDTO(Rifornimento rifornimento) {
        this.id = rifornimento.getId();
        this.dataRifornimento = rifornimento.getDataRifornimento();
        List<DettagliRifornimento> dettagli = rifornimento.getDettagli();
        this.righe = (dettagli == null ? List.<DettagliRifornimento>of() : dettagli).stream()
                .map(RigaRifornimentoDTO::new)
                .collect(Collectors.toList());
        this.totaleCosto = this.righe.stream().mapToDouble(RigaRifornimentoDTO::getSubtotale).sum();
    }
}
