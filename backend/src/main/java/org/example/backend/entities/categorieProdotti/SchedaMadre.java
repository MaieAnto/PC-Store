package org.example.backend.entities.categorieProdotti;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import org.example.backend.entities.Prodotto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "SCHEDA_MADRE")
public class SchedaMadre extends Prodotto {
    @Column(name = "socket", nullable = true)
    private String socket;

    @Column(name = "chipset", nullable = true)
    private String chipset;

    @Column(name = "formato", nullable = true)
    private String formato;

    @Column(name = "slot_ram", nullable = true)
    private int slotRam;
}
