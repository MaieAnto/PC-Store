package org.example.backend.repositories;

import org.example.backend.entities.Carrello;
import org.example.backend.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {

    Optional<Carrello> findByUtente(Utente utente);

    Optional<Carrello> findByUtente_Id(int idUtente);
}
