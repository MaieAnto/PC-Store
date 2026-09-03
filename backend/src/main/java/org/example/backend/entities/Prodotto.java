package org.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "PRODOTTO")
public abstract class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "codice", nullable = true, unique = true)
    private String codice;

    @Column(name = "marca", nullable = true)
    private String marca;

    @Column(name = "prezzo", nullable = false)
    private float prezzo;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "immagine_url", nullable = true)
    private String immagineUrl;

    @Version
    @Column(name = "versione", nullable = false)
    @JsonIgnore
    private long versione;

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<Recensione> recensioni;
}
