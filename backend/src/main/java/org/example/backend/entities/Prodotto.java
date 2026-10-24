package org.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "PRODOTTO")
public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "prezzo", nullable = false)
    private double prezzo;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "immagine_url", nullable = true)
    private String immagineUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "dettagli", nullable = true, length = 2000)
    private String dettagli;

    @Column(name = "disponibile", nullable = false)
    private boolean disponibile = true;

    @Version
    @Column(name = "versione", nullable = false)
    @JsonIgnore
    private long versione;

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<Recensione> recensioni;
}
