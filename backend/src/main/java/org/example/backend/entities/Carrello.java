package org.example.backend.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "CARRELLO")
public class Carrello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "utente", nullable = false, unique = true)
    private Utente utente;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DettagliCarrello> dettagli;
}
