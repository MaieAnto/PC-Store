package org.example.backend.services;

import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import org.example.backend.repositories.UtenteRepository;
import org.example.backend.eccezioni.CredenzialiNonValideException;
import org.example.backend.eccezioni.EmailGiaRegistrataException;
import org.example.backend.eccezioni.UtenteNonTrovatoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Utente registra(Utente utente) {
        if (utenteRepository.existsByEmail(utente.getEmail())) {
            throw new EmailGiaRegistrataException();
        }
        return utenteRepository.save(utente);
    }

    /* Verifica le credenziali e restituisce l'utente corrispondente.*/
    public Utente login(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(CredenzialiNonValideException::new);
        if (!utente.getPassword().equals(password)) {
            throw new CredenzialiNonValideException();
        }
        return utente;
    }

    public Utente trovaPerId(int id) {
        return utenteRepository.findById(id)
                .orElseThrow(() -> new UtenteNonTrovatoException("Utente con id " + id + " non trovato"));
    }

    public Utente trovaPerEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UtenteNonTrovatoException("Utente con email " + email + " non trovato"));
    }

    public List<Utente> trovaPerRuolo(Ruolo ruolo) {
        return utenteRepository.findByRuolo(ruolo);
    }

    public List<Utente> elencaTutti() {
        return utenteRepository.findAll();
    }
}
