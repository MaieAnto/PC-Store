package org.example.backend.repositories;

import org.example.backend.entities.DettagliOrdine;
import org.example.backend.entities.Prodotto;
import org.example.backend.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DettagliOrdineRepository extends JpaRepository<DettagliOrdine, Integer> {

    boolean existsByOrdine_AcquirenteAndProdotto(Utente acquirente, Prodotto prodotto);

    boolean existsByOrdine_Acquirente_IdAndProdotto_Id(int idAcquirente, int idProdotto);
}
