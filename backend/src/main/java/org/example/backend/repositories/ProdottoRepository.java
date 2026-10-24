package org.example.backend.repositories;

import org.example.backend.entities.Categoria;
import org.example.backend.entities.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdottoRepository extends JpaRepository<Prodotto, Integer> {

    @Query("SELECT p FROM Prodotto p WHERE p.disponibile = true "
            + "AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) "
            + "AND (:marca IS NULL OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) "
            + "AND (:categoria IS NULL OR p.categoria = :categoria) "
            + "AND (:prezzoMin IS NULL OR p.prezzo >= :prezzoMin) "
            + "AND (:prezzoMax IS NULL OR p.prezzo <= :prezzoMax)")
    Page<Prodotto> cercaCatalogo(@Param("nome") String nome, @Param("marca") String marca,
                                 @Param("categoria") Categoria categoria, @Param("prezzoMin") Double prezzoMin,
                                 @Param("prezzoMax") Double prezzoMax, Pageable pageable);

    // Le ricerche del catalogo considerano solo i prodotti in vendita (disponibile = true)
    List<Prodotto> findByDisponibileTrue();

    List<Prodotto> findByDisponibileFalse();
}
