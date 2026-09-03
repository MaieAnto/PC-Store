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
@Table(name = "SCHEDA_VIDEO")
public class SchedaVideo extends Prodotto {
    @Column(name = "chipset", nullable = true)
    private String chipset;

    @Column(name = "vram_gb", nullable = true)
    private int vramGb;

    @Column(name = "tipo_memoria", nullable = true)
    private String tipoMemoria;

    @Column(name = "tdp", nullable = true)
    private int tdp;

    @Column(name = "lunghezza_mm", nullable = true)
    private int lunghezzaMm;
}
