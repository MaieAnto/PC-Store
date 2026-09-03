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
@Table(name = "ARCHIVIAZIONE")
public class Archiviazione extends Prodotto {
    @Column(name = "capacita_gb", nullable = true)
    private int capacitaGb;

    @Column(name = "tipo", nullable = true)
    private String tipo;

    @Column(name = "interfaccia", nullable = true)
    private String interfaccia;

    @Column(name = "velocita_lettura", nullable = true)
    private int velocitaLettura;
}
