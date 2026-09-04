package org.example.backend.repositories.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CpuRepository extends JpaRepository<Cpu, Integer> {

    List<Cpu> findBySocketIgnoreCase(String socket);

    List<Cpu> findByNumeroCoreGreaterThanEqual(int numeroCore);

    List<Cpu> findByTdpLessThanEqual(int tdp);
}
