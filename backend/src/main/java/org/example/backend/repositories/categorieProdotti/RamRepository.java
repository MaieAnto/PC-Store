package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Ram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RamRepository extends JpaRepository<Ram, Integer> {

    List<Ram> findByCapacitaGbGreaterThanEqual(int capacitaGb);

    List<Ram> findByTipologiaIgnoreCase(String tipologia);

    List<Ram> findByFrequenzaMhzGreaterThanEqual(int frequenzaMhz);

    List<Ram> findByNumeroModuli(int numeroModuli);
}
