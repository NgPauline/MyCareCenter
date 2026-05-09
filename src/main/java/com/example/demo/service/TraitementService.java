package com.example.demo.service;

import com.example.demo.model.Resident;
import com.example.demo.model.Traitement;
import com.example.demo.repository.TraitementRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraitementService {

    private final TraitementRepository traitementRepo;

    public TraitementService(TraitementRepository traitementRepo) {
        this.traitementRepo = traitementRepo;
    }

    /* ---------------- VALIDATION METIER ---------------- */

    private void validateDates(Traitement t) {
        if (t.getDateDebut() != null && t.getDateFin() != null) {
            if (t.getDateFin().isBefore(t.getDateDebut())) {
                throw new IllegalArgumentException(
                        "La date de fin doit être postérieure à la date de début."
                );
            }
        }
    }

    /* ---------------- CRUD ---------------- */

    public List<Traitement> findAll() {
        return traitementRepo.findAllWithResident();
    }

    public Optional<Traitement> findById(Integer id) {
        return traitementRepo.findById(id);
    }

    public Traitement save(Traitement traitement) {
        validateDates(traitement);
        return traitementRepo.save(traitement);
    }

    public void update(Integer id, Traitement updated) {
        validateDates(updated);

        Traitement original = traitementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Traitement introuvable"));

        original.setMedicament(updated.getMedicament());
        original.setDosage(updated.getDosage());
        original.setFrequence(updated.getFrequence());
        original.setDateDebut(updated.getDateDebut());
        original.setDateFin(updated.getDateFin());

        traitementRepo.save(original);
    }

    public void delete(Integer id) {
        traitementRepo.deleteById(id);
    }

    public List<Traitement> findByResident(Resident resident) {
        Integer idDossier = resident.getDossierMedical().getIdDossier();
        return traitementRepo.findByDossierMedical_IdDossier(idDossier);
    }

    public List<Traitement> searchByResident(Resident resident, String keyword) {
        return traitementRepo.searchByResident(resident, keyword.toLowerCase());
    }
}
