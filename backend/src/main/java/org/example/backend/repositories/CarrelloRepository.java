package org.example.backend.repositories;

import org.example.backend.entities.Carrello;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {

    // Trova il carrello appartenente a un utente dato il suo id.
    Optional<Carrello> findByUtente_Id(int idUtente);
}
