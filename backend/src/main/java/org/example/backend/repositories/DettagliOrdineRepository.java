package org.example.backend.repositories;

import org.example.backend.entities.DettagliOrdine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DettagliOrdineRepository extends JpaRepository<DettagliOrdine, Integer> {

    // Verifica se un utente ha acquistato un certo prodotto
    boolean existsByOrdine_Acquirente_IdAndProdotto_Id(int idAcquirente, int idProdotto);
}
