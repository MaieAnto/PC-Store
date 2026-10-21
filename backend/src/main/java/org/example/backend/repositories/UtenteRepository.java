package org.example.backend.repositories;

import org.example.backend.entities.Utente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);

}
