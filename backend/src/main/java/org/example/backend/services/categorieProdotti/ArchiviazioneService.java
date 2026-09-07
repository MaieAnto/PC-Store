package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Archiviazione;
import org.example.backend.repositories.categorieProdotti.ArchiviazioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArchiviazioneService {

    private final ArchiviazioneRepository archiviazioneRepository;

    public ArchiviazioneService(ArchiviazioneRepository archiviazioneRepository) {
        this.archiviazioneRepository = archiviazioneRepository;
    }

    public List<Archiviazione> elencaTutti() {
        return archiviazioneRepository.findAll();
    }

    public List<Archiviazione> conAlmenoCapacita(int capacitaGb) {
        return archiviazioneRepository.findByCapacitaGbGreaterThanEqual(capacitaGb);
    }

    public List<Archiviazione> perTipo(String tipo) {
        return archiviazioneRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Archiviazione> perInterfaccia(String interfaccia) {
        return archiviazioneRepository.findByInterfacciaIgnoreCase(interfaccia);
    }

    public List<Archiviazione> conAlmenoVelocitaLettura(int velocitaLettura) {
        return archiviazioneRepository.findByVelocitaLetturaGreaterThanEqual(velocitaLettura);
    }
}
