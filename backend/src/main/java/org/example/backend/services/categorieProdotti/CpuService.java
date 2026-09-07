package org.example.backend.services.categorieProdotti;

import org.example.backend.entities.categorieProdotti.Cpu;
import org.example.backend.repositories.categorieProdotti.CpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CpuService {

    private final CpuRepository cpuRepository;

    public CpuService(CpuRepository cpuRepository) {
        this.cpuRepository = cpuRepository;
    }

    public List<Cpu> elencaTutti() {
        return cpuRepository.findAll();
    }

    public List<Cpu> perSocket(String socket) {
        return cpuRepository.findBySocketIgnoreCase(socket);
    }

    public List<Cpu> conAlmenoCore(int numeroCore) {
        return cpuRepository.findByNumeroCoreGreaterThanEqual(numeroCore);
    }

    public List<Cpu> conTdpMassimo(int tdp) {
        return cpuRepository.findByTdpLessThanEqual(tdp);
    }
}
