package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.DettagliOrdine;
import org.example.backend.entities.Ordine;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/* Ordine restituito al frontend */
@Getter
@Setter
public class OrdineDTO {

    private int id;
    private Date dataOrdine;
    private List<RigaOrdineDTO> righe;
    private double totale;

    // Costruisce il DTO a partire da un ordine, calcolando righe e totale.
    public OrdineDTO(Ordine ordine) {
        this.id = ordine.getId();
        this.dataOrdine = ordine.getDataOrdine();
        List<DettagliOrdine> dettagli = ordine.getDettagli();
        this.righe = (dettagli == null ? List.<DettagliOrdine>of() : dettagli).stream()
                .map(RigaOrdineDTO::new)
                .collect(Collectors.toList());
        this.totale = this.righe.stream().mapToDouble(RigaOrdineDTO::getSubtotale).sum();
    }
}
