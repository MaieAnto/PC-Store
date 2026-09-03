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
@Table(name = "CPU")
public class Cpu extends Prodotto {
    @Column(name = "socket", nullable = true)
    private String socket;

    @Column(name = "numero_core", nullable = true)
    private int numeroCore;

    @Column(name = "frequenza_base", nullable = true)
    private float frequenzaBase;

    @Column(name = "frequenza_boost", nullable = true)
    private float frequenzaBoost;

    @Column(name = "tdp", nullable = true)
    private int tdp;
}
