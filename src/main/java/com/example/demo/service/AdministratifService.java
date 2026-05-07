package com.example.demo.service;

import com.example.demo.model.Administratif;
import com.example.demo.repository.AdministratifRepository;
import com.example.demo.repository.PlanningRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministratifService {

    private final AdministratifRepository administratifRepository;
    private final PlanningRepository planningRepository;

    public AdministratifService(AdministratifRepository administratifRepository,
                                PlanningRepository planningRepository) {
        this.administratifRepository = administratifRepository;
        this.planningRepository = planningRepository;
    }

    public List<Administratif> findAll() {
        return administratifRepository.findAll();
    }

    public Optional<Administratif> findById(Integer id) {
        return administratifRepository.findById(id);
    }

    public Administratif save(Administratif administratif) {
        return administratifRepository.save(administratif);
    }

    public void update(Integer id, Administratif updated) {
        Administratif original = administratifRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Administratif introuvable"));

        // matricule non modifiable (cahier des charges)
        updated.setMatricule(original.getMatricule());

        original.setNom(updated.getNom());
        original.setPrenom(updated.getPrenom());
        original.setAdresse(updated.getAdresse());
        original.setTelephone(updated.getTelephone());
        original.setFonction(updated.getFonction());
        original.setSalaire(updated.getSalaire());

        administratifRepository.save(original);
    }

    public boolean existsByMatricule(String matricule) {
        return administratifRepository.existsByMatricule(matricule);
    }

    public boolean hasPlanningResponsable(Integer id) {
    return planningRepository.existsByResponsable_IdPersonne(id);
    }



    public void delete(Integer id) {
        administratifRepository.deleteById(id);
    }
}
