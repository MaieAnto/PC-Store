package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.SchedaMadre;
import org.example.backend.repositories.categorieProdotti.SchedaMadreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SchedaMadreService {

    private final SchedaMadreRepository schedaMadreRepository;

    public SchedaMadreService(SchedaMadreRepository schedaMadreRepository) {
        this.schedaMadreRepository = schedaMadreRepository;
    }

    public List<SchedaMadre> elencaTutti() {
        return schedaMadreRepository.findAll();
    }

    public List<SchedaMadre> perSocket(String socket) {
        return schedaMadreRepository.findBySocketIgnoreCase(socket);
    }

    public List<SchedaMadre> perChipset(String chipset) {
        return schedaMadreRepository.findByChipsetIgnoreCase(chipset);
    }

    public List<SchedaMadre> perFormato(String formato) {
        return schedaMadreRepository.findByFormatoIgnoreCase(formato);
    }

    public List<SchedaMadre> conAlmenoSlotRam(int slotRam) {
        return schedaMadreRepository.findBySlotRamGreaterThanEqual(slotRam);
    }
}
