package com.example.demo.service;

import com.example.demo.model.Famille;
import com.example.demo.model.Resident;
import com.example.demo.repository.FamilleRepository;
import com.example.demo.repository.ResidentRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FamilleService {

    private final FamilleRepository familleRepository;
    private final ResidentRepository residentRepository;

    public FamilleService(FamilleRepository familleRepository,
                          ResidentRepository residentRepository) {
        this.familleRepository = familleRepository;
        this.residentRepository = residentRepository;
    }

    public List<Famille> findAll() {
        return familleRepository.findAll();
    }

    public Optional<Famille> findById(Integer id) {
        return familleRepository.findById(id);
    }

    public Famille save(Famille famille) {
        return familleRepository.save(famille);
    }

    public void update(Integer id, Famille updated) {
        Famille original = familleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Famille introuvable"));

        original.setNom(updated.getNom());
        original.setPrenom(updated.getPrenom());
        original.setLienParente(updated.getLienParente());
        original.setType(updated.getType());
        original.setTelephone(updated.getTelephone());
        original.setEmail(updated.getEmail());
        original.setAdresse(updated.getAdresse());

        familleRepository.save(original);
    }

    public void associerResident(Famille famille, Resident resident) {
        if (!famille.getResidents().contains(resident)) {
            famille.getResidents().add(resident);
            resident.getFamilles().add(famille);
            familleRepository.save(famille);
            residentRepository.save(resident);
        }
    }

    public void dissocierResident(Famille famille, Resident resident) {
        famille.getResidents().remove(resident);
        resident.getFamilles().remove(famille);
        familleRepository.save(famille);
        residentRepository.save(resident);
    }

    public void delete(Integer id) {
        familleRepository.deleteById(id);
    }
    public List<Famille> findByResidentId(Integer idResident) {
    return familleRepository.findByResidents_IdPersonne(idResident);
}

    public List<Famille> searchByResidentId(Integer idResident, String keyword) {
        return familleRepository.searchByResidentId(idResident, keyword.toLowerCase());
    }

}
