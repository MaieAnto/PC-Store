package org.example.backend.dto;

import org.example.backend.entities.Ruolo;
import lombok.Getter;
import lombok.Setter;

/* Corpo della richiesta di registrazione */
@Getter
@Setter
public class DatiRegistrazione {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Ruolo ruolo;
}
