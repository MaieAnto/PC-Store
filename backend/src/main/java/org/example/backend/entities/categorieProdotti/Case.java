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
@Table(name = "CASE_PC")
public class Case extends Prodotto {
    @Column(name = "formato", nullable = true)
    private String formato;

    @Column(name = "numero_ventole", nullable = true)
    private int numeroVentole;

    @Column(name = "lunghezza_gpu_max", nullable = true)
    private int lunghezzaGpuMax;
}
