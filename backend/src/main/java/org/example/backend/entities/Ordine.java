package org.example.backend.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "ORDINE")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ordine", updatable = false)
    private Date dataOrdine;

    @ManyToOne
    @JoinColumn(name = "acquirente")
    private Utente acquirente;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
    private List<DettagliOrdine> dettagli;
}
