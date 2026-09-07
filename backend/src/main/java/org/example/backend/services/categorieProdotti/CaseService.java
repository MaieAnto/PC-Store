package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Case;
import org.example.backend.repositories.categorieProdotti.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> elencaTutti() {
        return caseRepository.findAll();
    }

    public List<Case> perFormato(String formato) {
        return caseRepository.findByFormatoIgnoreCase(formato);
    }

    public List<Case> conAlmenoVentole(int numeroVentole) {
        return caseRepository.findByNumeroVentoleGreaterThanEqual(numeroVentole);
    }

    public List<Case> perLunghezzaGpuMinima(int lunghezzaGpu) {
        return caseRepository.findByLunghezzaGpuMaxGreaterThanEqual(lunghezzaGpu);
    }
}
