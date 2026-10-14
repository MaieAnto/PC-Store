package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.entities.Utente;

@Getter
@Setter
public class RispostaLogin {

    private String token;
    private Utente utente;

    public RispostaLogin() {
    }

    public RispostaLogin(String token, Utente utente) {
        this.token = token;
        this.utente = utente;
    }
}
