package org.example.backend.repositories;

import org.example.backend.entities.Rifornimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RifornimentoRepository extends JpaRepository<Rifornimento, Integer> {

    List<Rifornimento> findByFornitore_IdOrderByDataRifornimentoDesc(int idFornitore);
}
