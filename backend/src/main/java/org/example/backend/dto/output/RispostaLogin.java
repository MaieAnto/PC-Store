package org.example.backend.dto.output;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Utente;

/* Risposta al login: token JWT e dati dell'utente autenticato. */
@Getter
@Setter
public class RispostaLogin {

    private String token;
    private UtenteDTO utente;

    public RispostaLogin(String token, Utente utente) {
        this.token = token;
        this.utente = new UtenteDTO(utente);
    }
}
