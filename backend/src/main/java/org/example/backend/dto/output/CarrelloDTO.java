package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Carrello;
import org.example.backend.entities.DettagliCarrello;

import java.util.List;
import java.util.stream.Collectors;

/* Carrello restituito al frontend */
@Getter
@Setter
public class CarrelloDTO {

    private Integer id;
    private List<RigaCarrelloDTO> righe;
    private double totale;

    // Costruisce il DTO a partire dall'entita' Carrello
    public CarrelloDTO(Carrello carrello) {
        this.id = carrello.getId();
        this.righe = mappaRighe(carrello.getDettagli());
        this.totale = calcolaTotale(this.righe);
    }

    /* Usato quando si hanno solo le righe e non l'entita' Carrello. */
    public CarrelloDTO(List<DettagliCarrello> dettagli) {
        this.id = null;
        this.righe = mappaRighe(dettagli);
        this.totale = calcolaTotale(this.righe);
    }

    private static List<RigaCarrelloDTO> mappaRighe(List<DettagliCarrello> dettagli) {
        if (dettagli == null) {
            return List.of();
        }
        return dettagli.stream().map(RigaCarrelloDTO::new).collect(Collectors.toList());
    }

    private static double calcolaTotale(List<RigaCarrelloDTO> righe) {
        return righe.stream().mapToDouble(RigaCarrelloDTO::getSubtotale).sum();
    }
}
