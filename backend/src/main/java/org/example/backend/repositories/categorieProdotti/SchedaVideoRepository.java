package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.SchedaVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedaVideoRepository extends JpaRepository<SchedaVideo, Integer> {

    List<SchedaVideo> findByChipsetIgnoreCase(String chipset);

    List<SchedaVideo> findByVramGbGreaterThanEqual(int vramGb);

    List<SchedaVideo> findByTipoMemoriaIgnoreCase(String tipoMemoria);

    List<SchedaVideo> findByLunghezzaMmLessThanEqual(int lunghezzaMm);
}
