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
@Table(name = "RIFORNIMENTO")
public class Rifornimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_rifornimento", updatable = false)
    private Date dataRifornimento;

    @ManyToOne
    @JoinColumn(name = "fornitore")
    private Utente fornitore;

    @OneToMany(mappedBy = "rifornimento", cascade = CascadeType.ALL)
    private List<DettagliRifornimento> dettagli;
}
