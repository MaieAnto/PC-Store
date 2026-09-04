package org.example.backend.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.util.Date;

/* un utente puo' recensire lo stesso prodotto una sola volta */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(
    name = "RECENSIONE",
    uniqueConstraints = @UniqueConstraint(
        name = "recensione_cliente_prodotto",
        columnNames = {"cliente", "prodotto"}
    )
)
public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cliente")
    private Utente autore;

    @ManyToOne
    @JoinColumn(name = "prodotto")
    @ToString.Exclude
    private Prodotto prodotto;

    @Column(name = "valutazione", nullable = false)
    private int valutazione;

    @Column(name = "commento", nullable = true)
    private String commento;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_recensione", updatable = false)
    private Date dataRecensione;
}
