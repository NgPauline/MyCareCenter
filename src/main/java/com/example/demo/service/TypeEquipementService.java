package com.example.demo.service;

import com.example.demo.model.TypeEquipement;
import com.example.demo.repository.TypeEquipementRepository;
import com.example.demo.repository.EquipementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeEquipementService {

    private final TypeEquipementRepository repo;
    private final EquipementRepository equipementRepository;

    public TypeEquipementService(TypeEquipementRepository repo,
                                 EquipementRepository equipementRepository) {
        this.repo = repo;
        this.equipementRepository = equipementRepository;
    }

    public List<TypeEquipement> findAll() {
        return repo.findAll();
    }

    public Optional<TypeEquipement> findById(Integer id) {
        return repo.findById(id);
    }

    public TypeEquipement save(TypeEquipement type) {
        return repo.save(type);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    // -----------------------------
    // STOCK : utilisés et restants
    // -----------------------------
    public long countUtilises(Integer typeId) {
        return equipementRepository.countByType_Id(typeId);
    }

    public long countRestants(TypeEquipement type) {
        long utilises = countUtilises(type.getId());
        return type.getQuantiteTotale() - utilises;
    }

    public long getTotalStock() {
    return findAll()
            .stream()
            .mapToLong(TypeEquipement::getQuantiteTotale)
            .sum();
}
}
