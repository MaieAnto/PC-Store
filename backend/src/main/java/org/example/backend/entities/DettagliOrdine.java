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
@Table(name = "DETTAGLIO_ORDINE")
public class DettagliOrdine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ordine_associato")
    @JsonIgnore
    @ToString.Exclude
    private Ordine ordine;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "prodotto")
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "prezzo_unitario", nullable = false)
    private float prezzoUnitario;
}
