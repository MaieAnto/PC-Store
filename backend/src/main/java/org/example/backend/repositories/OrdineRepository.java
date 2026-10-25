package org.example.backend.repositories;

import org.example.backend.entities.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineRepository extends JpaRepository<Ordine, Integer> {

    // Ordini di un acquirente, dal piu' recente al piu' vecchio.
    List<Ordine> findByAcquirente_IdOrderByDataOrdineDesc(int idAcquirente);
}
