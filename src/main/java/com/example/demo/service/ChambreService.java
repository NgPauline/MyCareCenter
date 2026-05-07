package com.example.demo.service;

import com.example.demo.model.Chambre;
import com.example.demo.model.Equipement;
import com.example.demo.model.Resident;
import com.example.demo.repository.ChambreRepository;
import com.example.demo.repository.EquipementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChambreService {

    private final ChambreRepository chambreRepository;
    private final EquipementRepository equipementRepository;

    public ChambreService(ChambreRepository chambreRepository,
                          EquipementRepository equipementRepository) {
        this.chambreRepository = chambreRepository;
        this.equipementRepository = equipementRepository;
    }

    public List<Chambre> findAll() {
        return chambreRepository.findAll();
    }

    public Optional<Chambre> findById(Integer id) {
        return chambreRepository.findById(id);
    }

    public Page<Chambre> findAll(Pageable pageable) {
        return chambreRepository.findAll(pageable);
    }

    public Chambre save(Chambre chambre) {

        // Génération automatique systématique
        Integer max = chambreRepository.findMaxNumero(); // ex: 12
        int next = max + 1;                              // ex: 13
        chambre.setNumero(String.format("CH%03d", next)); // ex: CH013

        return chambreRepository.save(chambre);
    }



    public void update(Integer id, Chambre updated) {
        Chambre original = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chambre introuvable"));

        original.setNumero(updated.getNumero());
        original.setType(updated.getType());
        original.setEtage(updated.getEtage());

        chambreRepository.save(original);
    }

    public boolean existsByNumero(String numero) {
        return chambreRepository.existsByNumero(numero);
    }

    public boolean isOccupee(Integer id) {
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chambre introuvable"));

        return chambre.getOccupant() != null;
    }

    public List<Chambre> findChambresLibres() {
        return chambreRepository.findByOccupantIsNull();
    }

    public List<Chambre> findChambresLibresOuActuelle(Resident resident) {
        List<Chambre> libres = chambreRepository.findByOccupantIsNull();

        if (resident.getChambre() != null) {
            libres.add(resident.getChambre());
        }

        return libres;
    }

    public void delete(Integer id) {
        chambreRepository.deleteById(id);
    }

    public List<Equipement> findByChambre(Integer idChambre) {
    return equipementRepository.findByChambre_IdChambre(idChambre);
    }

    public List<Chambre> search(String keyword) {
        return chambreRepository.search(keyword.toLowerCase());
    }

    public Page<Chambre> search(String keyword, Pageable pageable) {
        return chambreRepository.search(keyword.toLowerCase(), pageable);
    }

}
