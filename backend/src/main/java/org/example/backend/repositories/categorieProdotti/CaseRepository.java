package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Integer> {

    List<Case> findByFormatoIgnoreCase(String formato);

    List<Case> findByNumeroVentoleGreaterThanEqual(int numeroVentole);

    List<Case> findByLunghezzaGpuMaxGreaterThanEqual(int lunghezzaGpuMax);
}
