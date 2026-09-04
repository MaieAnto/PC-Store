package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Archiviazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchiviazioneRepository extends JpaRepository<Archiviazione, Integer> {

    List<Archiviazione> findByCapacitaGbGreaterThanEqual(int capacitaGb);

    List<Archiviazione> findByTipoIgnoreCase(String tipo);

    List<Archiviazione> findByInterfacciaIgnoreCase(String interfaccia);

    List<Archiviazione> findByVelocitaLetturaGreaterThanEqual(int velocitaLettura);
}
