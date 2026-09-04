package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Alimentatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlimentatoreRepository extends JpaRepository<Alimentatore, Integer> {

    List<Alimentatore> findByPotenzaWattGreaterThanEqual(int potenzaWatt);

    List<Alimentatore> findByCertificazioneIgnoreCase(String certificazione);

    List<Alimentatore> findByModulare(boolean modulare);

    List<Alimentatore> findByFormatoIgnoreCase(String formato);
}
