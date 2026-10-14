package org.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

/* Corpo della richiesta di login */
@Getter
@Setter
public class CredenzialiLogin {

    private String email;
    private String password;
}
