package com.example.demo.service;

import com.example.demo.model.Chambre;
import com.example.demo.model.Equipement;
import com.example.demo.model.TypeEquipement;
import com.example.demo.repository.EquipementRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final ChambreService chambreService;
    private final TypeEquipementService typeEquipementService;

    public EquipementService(EquipementRepository equipementRepository,
                             ChambreService chambreService,
                             TypeEquipementService typeEquipementService) {
        this.equipementRepository = equipementRepository;
        this.chambreService = chambreService;
        this.typeEquipementService = typeEquipementService;
    }

    // -----------------------------
    // FIND ALL
    // -----------------------------
    public List<Equipement> findAll() {
        return equipementRepository.findAllByOrderByType_NomAsc();
    }

    public Page<Equipement> findAll(Pageable pageable) {
        return equipementRepository.findAllByOrderByType_NomAsc(pageable);
    }

    // -----------------------------
    // FIND BY ID
    // -----------------------------
    public Optional<Equipement> findById(Integer id) {
        return equipementRepository.findById(id);
    }

    // -----------------------------
    // SAVE
    // -----------------------------
    public Equipement save(Equipement equipement) {
        return equipementRepository.save(equipement);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    public void update(Integer id, Equipement updated) {
        Equipement original = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable"));

        original.setEtat(updated.getEtat());
        original.setPhotoPath(updated.getPhotoPath());
        original.setChambre(updated.getChambre());
        original.setType(updated.getType());

        equipementRepository.save(original);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    public void delete(Integer id) {
        equipementRepository.deleteById(id);
    }

    // -----------------------------
    // FIND BY CHAMBRE
    // -----------------------------
    public List<Equipement> findByChambre(Integer idChambre) {
        return equipementRepository.findByChambre_IdChambre(idChambre);
    }

    // -----------------------------
    // SEARCH
    // -----------------------------
    public List<Equipement> search(String keyword) {
        return equipementRepository.search(keyword);
    }

    public Page<Equipement> search(String keyword, Pageable pageable) {
        return equipementRepository.search(keyword, pageable);
    }

    // -----------------------------
    // COUNT
    // -----------------------------
    public long count() {
        return equipementRepository.count();
    }

    public long countEnChambre() {
        return equipementRepository.countByChambreIsNotNull();
    }

    public long countByType(Integer typeId) {
    return equipementRepository.countByType_Id(typeId);
}


    // -----------------------------
    // CONTRAINTE : un type par chambre max
    // -----------------------------
    public boolean existsByChambreAndType(Integer idChambre, Integer idType) {
        Chambre chambre = chambreService.findById(idChambre)
                .orElseThrow(() -> new IllegalArgumentException("Chambre introuvable"));

        TypeEquipement type = typeEquipementService.findById(idType)
                .orElseThrow(() -> new IllegalArgumentException("Type introuvable"));

        return equipementRepository.findByChambreAndType(chambre, type).isPresent();
    }
}

