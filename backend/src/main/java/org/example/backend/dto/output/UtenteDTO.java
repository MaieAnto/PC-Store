package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;

/* Dati dell'utente restituiti al frontend */
@Getter
@Setter
public class UtenteDTO {

    private int id;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;

    public UtenteDTO(Utente utente) {
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        this.email = utente.getEmail();
        this.ruolo = utente.getRuolo();
    }
}
