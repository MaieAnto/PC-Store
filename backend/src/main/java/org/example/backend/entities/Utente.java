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
@Table(name = "UTENTE")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cognome", nullable = false)
    private String cognome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo", nullable = false)
    private Ruolo ruolo;

    @OneToMany(mappedBy = "acquirente", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<Ordine> ordini;

    @OneToMany(mappedBy = "autore", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<Recensione> recensioni;

    @OneToOne(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private Carrello carrello;

    @OneToMany(mappedBy = "fornitore", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<Rifornimento> rifornimenti;
}
