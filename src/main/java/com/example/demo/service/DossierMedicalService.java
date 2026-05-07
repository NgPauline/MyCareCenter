package com.example.demo.service;

import com.example.demo.model.DossierMedical;
import com.example.demo.model.Resident;
import com.example.demo.repository.DossierMedicalRepository;
import com.example.demo.repository.ResidentRepository;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class DossierMedicalService  {

    private final DossierMedicalRepository dossierRepo;
    private final ResidentRepository residentRepo;

    public DossierMedicalService(DossierMedicalRepository dossierRepo,
                                 ResidentRepository residentRepo) {
        this.dossierRepo = dossierRepo;
        this.residentRepo = residentRepo;
    }

    /* ------------------ MISE À JOUR ------------------ */
    public void updateForResident(Integer residentId, DossierMedical updated) {

        Resident resident = residentRepo.findById(residentId)
                .orElseThrow(() -> new IllegalArgumentException("Résident introuvable"));

        DossierMedical dossier = resident.getDossierMedical();

        if (dossier == null) {
            throw new IllegalStateException("Le résident n'a pas de dossier médical");
        }

        dossier.setGroupeSanguin(updated.getGroupeSanguin());
        dossier.setAllergies(updated.getAllergies());
        dossier.setObservationsGenerales(updated.getObservationsGenerales());

        dossierRepo.save(dossier);
    }

    /* ------------------ CRÉATION AUTOMATIQUE ------------------ */
    public DossierMedical createForResident(Resident resident) {

        // Si déjà existant → on renvoie
        if (resident.getDossierMedical() != null) {
            return resident.getDossierMedical();
        }

        DossierMedical dossier = new DossierMedical();
        dossier.setDateCreation(LocalDate.now());
        dossier.setGroupeSanguin("Inconnu");
        dossier.setAllergies("");
        dossier.setObservationsGenerales("");

        // Synchronisation des deux côtés
        dossier.setResident(resident);
        resident.setDossierMedical(dossier);

        return dossierRepo.save(dossier);
    }
}
