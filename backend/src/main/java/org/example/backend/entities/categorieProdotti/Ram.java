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
@Table(name = "RAM")
public class Ram extends Prodotto {
    @Column(name = "capacita_gb", nullable = true)
    private int capacitaGb;

    @Column(name = "tipologia", nullable = true)
    private String tipologia;

    @Column(name = "frequenza_mhz", nullable = true)
    private int frequenzaMhz;

    @Column(name = "numero_moduli", nullable = true)
    private int numeroModuli;

    @Column(name = "latenza_cl", nullable = true)
    private int latenzaCl;
}
