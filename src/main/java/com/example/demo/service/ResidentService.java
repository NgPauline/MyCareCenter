package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.ResidentRepository;
import com.example.demo.repository.FamilleRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final FamilleRepository familleRepository;

    public ResidentService(ResidentRepository residentRepository,
                           FamilleRepository familleRepository) {
        this.residentRepository = residentRepository;
        this.familleRepository = familleRepository;
    }

    /* -----------------------------
       1. Compter les résidents
       ----------------------------- */
    public long count() {
        return residentRepository.count();
    }

    /* -----------------------------
       2. Derniers résidents
       ----------------------------- */
    public List<Resident> findLast5() {
        return residentRepository.findTop5ByOrderByIdPersonneDesc();
    }

    /* -----------------------------
       3. Trouver tous les résidents
       ----------------------------- */
    public List<Resident> findAll() {
        return residentRepository.findAllByOrderByNomAscPrenomAsc();
    }

    public Page<Resident> findAll(Pageable pageable) {
        return residentRepository.findAllByOrderByNomAscPrenomAsc(pageable);
    }
    /* -----------------------------
       4. Trouver par ID
       ----------------------------- */
    public Optional<Resident> findById(Integer id) {
        return residentRepository.findById(id);
    }

    /* -----------------------------
       5. Sauvegarder un résident
       ----------------------------- */
    public Resident save(Resident resident) {

        if (resident.calculerAge() < 21) {
            throw new IllegalArgumentException("Le résident doit avoir au moins 21 ans");
        }

        // Génération automatique de l'idResident
        if (resident.getIdResident() == null) {
            Integer nextId = residentRepository.findMaxIdResident() + 1;
            resident.setIdResident(nextId);
        }

        // Création automatique des données minimales
        creerDossierMedicalSiAbsent(resident);
        //creerFamilleParDefaut(resident);
        //creerFactureInitiale(resident);

        return residentRepository.save(resident);
    }

    /* -----------------------------
       6. Mise à jour
       ----------------------------- */
    public void update(Integer id, Resident updated) {
        Resident original = residentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Résident introuvable"));

        original.setNom(updated.getNom());
        original.setPrenom(updated.getPrenom());
        original.setDateNaissance(updated.getDateNaissance());
        original.setTypeHandicap(updated.getTypeHandicap());
        original.setNiveauAutonomie(updated.getNiveauAutonomie());
        original.setDateAdmission(updated.getDateAdmission());
        original.setStatut(updated.getStatut());
        original.setChambre(updated.getChambre());

        residentRepository.save(original);
    }

    /* -----------------------------
       7. Résidents sans chambre
       ----------------------------- */
    public List<Resident> findResidentsSansChambre() {
        return residentRepository.findByChambreIsNull();
    }

    /* -----------------------------
       8. Recherche
       ----------------------------- */
    public List<Resident> search(String keyword) {
        return residentRepository.search(keyword);
    }

    public Page<Resident> search(String keyword, Pageable pageable) {
        return residentRepository.search(keyword, pageable);
    }

    /* -----------------------------
       9. Suppression
       ----------------------------- */
    public void delete(Integer id) {
        residentRepository.deleteById(id);
    }

    /* -----------------------------
       10. Statistiques mensuelles
       ----------------------------- */
    public List<Long> getMonthlyCount(int months) {
        LocalDate start = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        List<Object[]> rows = residentRepository.countGroupedByMonth(start);
        Map<String, Long> map = new LinkedHashMap<>();

        for (int i = months - 1; i >= 0; i--) {
            map.put(YearMonth.now().minusMonths(i).toString(), 0L);
        }

        for (Object[] row : rows) {
            map.put(row[0].toString(), ((Number) row[1]).longValue());
        }

        return new ArrayList<>(map.values());
    }

    /* -----------------------------
       11. Création automatique
       ----------------------------- */

    private void creerDossierMedicalSiAbsent(Resident resident) {
        if (resident.getDossierMedical() == null) {
            DossierMedical dossier = new DossierMedical(
                    LocalDate.now(),
                    "Inconnu",
                    "Aucune",
                    "RAS"
            );
            dossier.setResident(resident);
            resident.setDossierMedical(dossier);
        }
    }

    private void creerFamilleParDefaut(Resident resident) {
        if (resident.getFamilles() == null || resident.getFamilles().isEmpty()) {

            Famille f = new Famille(
                    "Non renseigné",
                    "Non renseigné",
                    "Inconnu",
                    "Tuteur",
                    "+32000000000",
                    "inconnu@mail.com",
                    "Rue Inconnue 1, 1000 Bruxelles"
            );

            // Sauvegarde AVANT association
            familleRepository.save(f);

            // Association bidirectionnelle
            f.getResidents().add(resident);
            resident.getFamilles().add(f);
        }
    }

    private void creerFactureInitiale(Resident resident) {
        Facture facture = new Facture(
                LocalDate.now(),
                0.0,
                "EN_ATTENTE",
                resident
        );
        resident.getFactures().add(facture);
    }
}

