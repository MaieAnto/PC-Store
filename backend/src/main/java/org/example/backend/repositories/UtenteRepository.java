package org.example.backend.repositories;

import org.example.backend.entities.Utente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    // Trova un utente dalla sua email
    Optional<Utente> findByEmail(String email);

    // Verifica se esiste gia' un utente con quella email
    boolean existsByEmail(String email);

}
