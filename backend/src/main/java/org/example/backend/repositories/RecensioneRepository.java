package org.example.backend.repositories;

import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Recensione;
import org.example.backend.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Integer> {

    // Recensioni di un prodotto, dalla piu' recente alla piu' vecchia.
    List<Recensione> findByProdotto_IdOrderByDataRecensioneDesc(int idProdotto);

    // Verifica se un utente ha gia' recensito un prodotto
    boolean existsByAutoreAndProdotto(Utente autore, Prodotto prodotto);
}
