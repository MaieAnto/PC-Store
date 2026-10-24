package org.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "DETTAGLIO_RIFORNIMENTO")
public class DettagliRifornimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rifornimento_associato")
    @JsonIgnore
    @ToString.Exclude
    private Rifornimento rifornimento;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "prodotto")
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "costo_unitario", nullable = true)
    private double costoUnitario;
}
