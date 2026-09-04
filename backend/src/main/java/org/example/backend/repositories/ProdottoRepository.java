package org.example.backend.repositories;

import org.example.backend.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdottoRepository extends JpaRepository<Prodotto, Integer> {

    Optional<Prodotto> findByCodice(String codice);

    boolean existsByCodice(String codice);

    List<Prodotto> findByNomeContainingIgnoreCase(String nome);

    List<Prodotto> findByMarcaIgnoreCase(String marca);

    List<Prodotto> findByPrezzoBetween(float prezzoMin, float prezzoMax);

    List<Prodotto> findByQuantitaGreaterThan(int quantita);
}
