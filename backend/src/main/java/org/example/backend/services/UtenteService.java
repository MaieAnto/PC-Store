package org.example.backend.services;

import org.example.backend.dto.input.DatiRegistrazione;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.UtenteRepository;
import org.example.backend.eccezioni.CredenzialiNonValideException;
import org.example.backend.eccezioni.EmailGiaRegistrataException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* Registra un nuovo utente, rifiutando email gia' presenti e cifrando la password. */
    @Transactional
    public Utente registra(DatiRegistrazione dati) {
        if (utenteRepository.existsByEmail(dati.getEmail())) {
            throw new EmailGiaRegistrataException();
        }
        Utente utente = new Utente();
        utente.setNome(dati.getNome());
        utente.setCognome(dati.getCognome());
        utente.setEmail(dati.getEmail());
        utente.setPassword(passwordEncoder.encode(dati.getPassword()));
        utente.setRuolo(dati.getRuolo());
        return utenteRepository.save(utente);
    }

    /* Verifica le credenziali e restituisce l'utente, altrimenti lancia un'eccezione. */
    public Utente login(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email).orElseThrow(CredenzialiNonValideException::new);

        if (!passwordEncoder.matches(password, utente.getPassword())) {
            throw new CredenzialiNonValideException();
        }
        return utente;
    }

    /* Trova l'utente dalla sua email */
    public Utente trovaPerEmail(String email) {
        return utenteRepository.findByEmail(email).orElseThrow(CredenzialiNonValideException::new);
    }

}
