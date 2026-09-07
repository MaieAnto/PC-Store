package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Alimentatore;
import org.example.backend.repositories.categorieProdotti.AlimentatoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlimentatoreService {

    private final AlimentatoreRepository alimentatoreRepository;

    public AlimentatoreService(AlimentatoreRepository alimentatoreRepository) {
        this.alimentatoreRepository = alimentatoreRepository;
    }

    public List<Alimentatore> elencaTutti() {
        return alimentatoreRepository.findAll();
    }

    public List<Alimentatore> conAlmenoPotenza(int potenzaWatt) {
        return alimentatoreRepository.findByPotenzaWattGreaterThanEqual(potenzaWatt);
    }

    public List<Alimentatore> perCertificazione(String certificazione) {
        return alimentatoreRepository.findByCertificazioneIgnoreCase(certificazione);
    }

    public List<Alimentatore> perModulare(boolean modulare) {
        return alimentatoreRepository.findByModulare(modulare);
    }

    public List<Alimentatore> perFormato(String formato) {
        return alimentatoreRepository.findByFormatoIgnoreCase(formato);
    }
}
