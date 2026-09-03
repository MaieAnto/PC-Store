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
@Table(name = "DETTAGLIO_CARRELLO")
public class DettagliCarrello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "carrello")
    @JsonIgnore
    @ToString.Exclude
    private Carrello carrello;

    @ManyToOne
    @JoinColumn(name = "prodotto")
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private int quantita;
}
