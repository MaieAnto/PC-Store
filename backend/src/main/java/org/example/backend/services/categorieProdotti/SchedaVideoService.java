package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.SchedaVideo;
import org.example.backend.repositories.categorieProdotti.SchedaVideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SchedaVideoService {

    private final SchedaVideoRepository schedaVideoRepository;

    public SchedaVideoService(SchedaVideoRepository schedaVideoRepository) {
        this.schedaVideoRepository = schedaVideoRepository;
    }

    public List<SchedaVideo> elencaTutti() {
        return schedaVideoRepository.findAll();
    }

    public List<SchedaVideo> perChipset(String chipset) {
        return schedaVideoRepository.findByChipsetIgnoreCase(chipset);
    }

    public List<SchedaVideo> conAlmenoVram(int vramGb) {
        return schedaVideoRepository.findByVramGbGreaterThanEqual(vramGb);
    }

    public List<SchedaVideo> perTipoMemoria(String tipoMemoria) {
        return schedaVideoRepository.findByTipoMemoriaIgnoreCase(tipoMemoria);
    }

    public List<SchedaVideo> conLunghezzaMassima(int lunghezzaMm) {
        return schedaVideoRepository.findByLunghezzaMmLessThanEqual(lunghezzaMm);
    }
}
