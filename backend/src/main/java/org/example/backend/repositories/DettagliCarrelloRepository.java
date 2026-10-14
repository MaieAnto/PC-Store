package org.example.backend.repositories;

import org.example.backend.entities.DettagliCarrello;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DettagliCarrelloRepository extends JpaRepository<DettagliCarrello, Integer> {

    // Rimuove il prodotto da tutti i carrelli (usato prima di eliminarlo dal catalogo).
    void deleteByProdotto_Id(int idProdotto);
}
