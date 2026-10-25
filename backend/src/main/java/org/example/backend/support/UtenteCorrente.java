package org.example.backend.support;

import org.example.backend.services.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/* Ricava i dati dell'utente autenticato a partire dal token */
@Component
public class UtenteCorrente {

    private final UtenteService utenteService;

    public UtenteCorrente(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    public int id(Authentication auth) {
        return utenteService.trovaPerEmail(auth.getName()).getId();
    }
}
