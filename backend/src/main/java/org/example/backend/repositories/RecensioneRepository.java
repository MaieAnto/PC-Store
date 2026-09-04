package org.example.backend.repositories;

import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Recensione;
import org.example.backend.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Integer> {

    List<Recensione> findByProdotto(Prodotto prodotto);

    List<Recensione> findByProdotto_IdOrderByDataRecensioneDesc(int idProdotto);

    List<Recensione> findByAutore(Utente autore);

    boolean existsByAutoreAndProdotto(Utente autore, Prodotto prodotto);
}
