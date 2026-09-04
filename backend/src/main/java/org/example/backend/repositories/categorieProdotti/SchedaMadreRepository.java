package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.SchedaMadre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedaMadreRepository extends JpaRepository<SchedaMadre, Integer> {

    List<SchedaMadre> findBySocketIgnoreCase(String socket);

    List<SchedaMadre> findByChipsetIgnoreCase(String chipset);

    List<SchedaMadre> findByFormatoIgnoreCase(String formato);

    List<SchedaMadre> findBySlotRamGreaterThanEqual(int slotRam);
}
