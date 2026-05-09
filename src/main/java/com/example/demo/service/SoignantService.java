package com.example.demo.service;

import com.example.demo.model.Soignant;
import com.example.demo.repository.SoignantRepository;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ConsultationRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SoignantService {

    private final SoignantRepository soignantRepository;
    private final ConsultationRepository consultationRepository;
    private final ActiviteRepository activiteRepository;

    public SoignantService(SoignantRepository soignantRepository,
                           ConsultationRepository consultationRepository,
                           ActiviteRepository activiteRepository) {
        this.soignantRepository = soignantRepository;
        this.consultationRepository = consultationRepository;
        this.activiteRepository = activiteRepository;
    }

    public List<Soignant> findAll() {
        return soignantRepository.findAll();
    }

    public Optional<Soignant> findById(Integer id) {
        return soignantRepository.findById(id);
    }

    public Optional<Soignant> findByMatricule(String matricule) {
        return Optional.ofNullable(soignantRepository.findByMatricule(matricule));
    }

    public boolean existsByMatricule(String matricule) {
        return soignantRepository.existsByMatricule(matricule);
    }

    public boolean hasConsultations(Integer id) {
        return soignantRepository.findById(id)
                .map(soignant -> !consultationRepository.findBySoignant(soignant).isEmpty())
                .orElse(false);
    }

    public boolean hasActivites(Integer id) {
        return soignantRepository.findById(id)
                .map(soignant -> !activiteRepository.findByResponsable(soignant).isEmpty())
                .orElse(false);
    }

    public Soignant save(Soignant soignant) {

        if (soignant.calculerAge() < 18) {
            throw new IllegalArgumentException("Le soignant doit avoir au moins 18 ans");
        }

        if (soignant.getSalaire() <= 0) {
            throw new IllegalArgumentException("Le salaire doit être positif");
        }

        return soignantRepository.save(soignant);
    }

    public void update(Integer id, Soignant updated) {

        Soignant original = soignantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Soignant introuvable"));

        updated.setMatricule(original.getMatricule());

        original.setNom(updated.getNom());
        original.setPrenom(updated.getPrenom());
        original.setAdresse(updated.getAdresse());
        original.setTelephone(updated.getTelephone());
        original.setPoste(updated.getPoste());
        original.setSalaire(updated.getSalaire());
        original.setDiplome(updated.getDiplome());
        original.setSpecialite(updated.getSpecialite());
        original.setRoleApp(updated.getRoleApp());

        soignantRepository.save(original);
    }

    public void delete(Integer id) {

        if (hasConsultations(id)) {
            throw new IllegalStateException("Impossible de supprimer un soignant ayant des consultations");
        }

        if (hasActivites(id)) {
            throw new IllegalStateException("Impossible de supprimer un soignant responsable d'activités");
        }

        soignantRepository.deleteById(id);
    }
}
