package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Ram;
import org.example.backend.repositories.categorieProdotti.RamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RamService {

    private final RamRepository ramRepository;

    public RamService(RamRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    public List<Ram> elencaTutti() {
        return ramRepository.findAll();
    }

    public List<Ram> conAlmenoCapacita(int capacitaGb) {
        return ramRepository.findByCapacitaGbGreaterThanEqual(capacitaGb);
    }

    public List<Ram> perTipologia(String tipologia) {
        return ramRepository.findByTipologiaIgnoreCase(tipologia);
    }

    public List<Ram> conAlmenoFrequenza(int frequenzaMhz) {
        return ramRepository.findByFrequenzaMhzGreaterThanEqual(frequenzaMhz);
    }

    public List<Ram> perNumeroModuli(int numeroModuli) {
        return ramRepository.findByNumeroModuli(numeroModuli);
    }
}
