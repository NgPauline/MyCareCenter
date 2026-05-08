package com.example.demo.service;

import com.example.demo.model.Employe;
import com.example.demo.repository.EmployeRepository;
import com.example.demo.repository.PlanningRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final PlanningRepository planningRepository;

    public EmployeService(EmployeRepository employeRepository,
                          PlanningRepository planningRepository) {
        this.employeRepository = employeRepository;
        this.planningRepository = planningRepository;
    }

    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    public Page<Employe> findAll(Pageable pageable) {
        return employeRepository.findAll(pageable);
    }

    public Optional<Employe> findById(Integer id) {
        return employeRepository.findById(id);
    }

    public boolean existsByMatricule(String matricule) {
        return employeRepository.existsByMatricule(matricule);
    }

    public boolean hasPlanningResponsable(Integer id) {
        return planningRepository.existsByResponsable_IdPersonne(id);
    }

    public long count() {
        return employeRepository.count();
    }

    public Employe save(Employe employe) {

        //  RÈGLE MÉTIER : un seul directeur
        if ("DIRECTEUR".equals(employe.getRoleApp())
                && employeRepository.existsByRoleApp("DIRECTEUR")) {
            throw new IllegalStateException("Il existe déjà un directeur dans le système.");
        }

        // RÈGLE MÉTIER : âge minimum
        if (employe.calculerAge() < 21) {
            throw new IllegalArgumentException("L'employé doit avoir au moins 21 ans.");
        }

        //  GÉNÉRATION AUTOMATIQUE DU MATRICULE
        if (employe.getMatricule() == null || employe.getMatricule().isBlank()) {

            String prefix;

            switch (employe.getRoleApp()) {
                case "SOIGNANT":
                    prefix = "SOI";
                    break;
                case "EDUCATEUR":
                    prefix = "EDU";
                    break;
                case "ADMINISTRATIF":
                    prefix = "EMP";
                    break;
                case "FINANCE":
                    prefix = "FIN";
                    break;
                case "DIRECTEUR":
                    prefix = "DIR";
                    break;
                default:
                    prefix = "EMP";
            }

            long count = employeRepository.countByMatriculeStartingWith(prefix);

            String matricule = prefix + String.format("%03d", count + 1);

            employe.setMatricule(matricule);
        }

        return employeRepository.save(employe);
    }


    public void update(Integer id, Employe updated) {

        Employe original = employeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));

        // 🔥 RÈGLE MÉTIER : empêcher un deuxième directeur lors d'une modification
        if ("DIRECTEUR".equals(updated.getRoleApp())
                && !original.getRoleApp().equals("DIRECTEUR")
                && employeRepository.existsByRoleApp("DIRECTEUR")) {
            throw new IllegalStateException("Il existe déjà un directeur dans le système.");
        }

        updated.setMatricule(original.getMatricule());

        original.setNom(updated.getNom());
        original.setPrenom(updated.getPrenom());
        original.setAdresse(updated.getAdresse());
        original.setTelephone(updated.getTelephone());
        original.setPoste(updated.getPoste());
        original.setSalaire(updated.getSalaire());
        original.setRoleApp(updated.getRoleApp());

        employeRepository.save(original);
    }

    public void delete(Integer id) {
        employeRepository.deleteById(id);
    }

    public List<Employe> search(String keyword) {
        return employeRepository.search(keyword.toLowerCase());
    }

    public Page<Employe> search(String keyword, Pageable pageable) {
        return employeRepository.search(keyword.toLowerCase(), pageable);
    }
  
    public List<Employe> findBySoignantOuEducateur() {
    return employeRepository.findAll()
        .stream()
        .filter(e -> e.getRoleApp().equals("SOIGNANT") || e.getRoleApp().equals("EDUCATEUR"))
        .collect(Collectors.toList());
}

    public Optional<Employe> findByMatricule(String matricule) {
        Employe e = employeRepository.findByMatricule(matricule);
        return Optional.ofNullable(e);
    }

    public List<Employe> findByRole(String role) {
        return employeRepository.findByRoleApp(role);
    }
}
