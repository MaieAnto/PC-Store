package org.example.backend.repositories;

import org.example.backend.entities.Ruolo;
import org.example.backend.entities.Utente;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utente> findByRuolo(Ruolo ruolo);
}
