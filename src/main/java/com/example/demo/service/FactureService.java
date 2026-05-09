package com.example.demo.service;

import com.example.demo.model.Facture;
import com.example.demo.model.Resident;
import com.example.demo.repository.FactureRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;

    public FactureService(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    // -----------------------------
    // 1. Compter toutes les factures
    // -----------------------------
    public long count() {
        return factureRepository.count();
    }

    // -----------------------------
    // 2. Compter les factures EN_ATTENTE (Dashboard)
    // -----------------------------
    public long countEnAttente() {
        return factureRepository.countByStatut("EN_ATTENTE");
    }

    // -----------------------------
    // 3. Dernières factures (Dashboard)
    // -----------------------------
    public List<Facture> findLast5() {
        return factureRepository.findTop5ByOrderByIdFactureDesc();
    }

    // -----------------------------
    // 4. Trouver toutes les factures
    // -----------------------------
    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    public Page<Facture> findAll(Pageable pageable) {
        return factureRepository.findAll(pageable);
    }

    // -----------------------------
    // 5. Trouver par ID
    // -----------------------------
    public Optional<Facture> findById(Integer id) {
        return factureRepository.findById(id);
    }

    // -----------------------------
    // 6. Factures d’un résident
    // -----------------------------
    public List<Facture> findByResident(Resident resident) {
        return factureRepository.findByResident(resident);
    }

    // -----------------------------
    // 7. Sauvegarder une facture
    // -----------------------------
    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    // -----------------------------
    // 8. Mettre à jour une facture
    // -----------------------------
    public void update(Integer id, Facture updated) {
        Facture original = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));

        // Une facture payée ne peut pas être modifiée
        if (original.getSoldeRestant() <= 0) {
            throw new IllegalStateException("Impossible de modifier une facture payée");
        }

        original.setMontant(updated.getMontant());
        original.setStatut(updated.getStatut());
        original.setDateEmission(updated.getDateEmission());
        original.setResident(updated.getResident());

        factureRepository.save(original);
    }

    // -----------------------------
    // 9. Vérifier si une facture a des paiements
    // -----------------------------
    public boolean hasPaiements(Integer id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));

        return !facture.getPaiements().isEmpty();
    }

    // -----------------------------
    // 10. Vérifier si un paiement peut être ajouté
    // -----------------------------
    public boolean peutAjouterPaiement(Facture facture, double montantPaiement) {
        if (montantPaiement <= 0) return false; // sécurité
        return montantPaiement <= facture.getSoldeRestant();
    }

    // -----------------------------
    // 11. Vérifier si un résident a des factures impayées
    // -----------------------------
    public boolean hasFacturesImpayees(Integer residentId) {
        return factureRepository.existsByResident_IdPersonneAndStatut(residentId, "EN_ATTENTE");
    }

    // -----------------------------
    // 12. Supprimer une facture
    // -----------------------------
    public void delete(Integer id) {
        factureRepository.deleteById(id);
    }

    public List<Facture> search(String keyword) {
        return factureRepository.search(keyword.toLowerCase());
    }

    public Page<Facture> search(String keyword, Pageable pageable) {
        return factureRepository.search(keyword.toLowerCase(), pageable);
    }

public List<Long> getMonthlyAmounts(int months) {
    LocalDate start = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
    List<Object[]> rows = factureRepository.sumMontantGroupedByMonth(start);
    Map<String, Long> map = new LinkedHashMap<>();
    for (int i = months - 1; i >= 0; i--) {
        map.put(YearMonth.now().minusMonths(i).toString(), 0L);
    }
    for (Object[] row : rows) {
        map.put(row[0].toString(), ((Number) row[1]).longValue());
    }
    return new ArrayList<>(map.values());
}

public List<Long> getMonthlyPaid(int months) {
    LocalDate start = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
    List<Object[]> rows = factureRepository.sumMontantPayeeGroupedByMonth(start);
    Map<String, Long> map = new LinkedHashMap<>();
    for (int i = months - 1; i >= 0; i--) {
        map.put(YearMonth.now().minusMonths(i).toString(), 0L);
    }
    for (Object[] row : rows) {
        map.put(row[0].toString(), ((Number) row[1]).longValue());
    }
    return new ArrayList<>(map.values());
}    

    public List<Facture> searchByResident(Resident resident, String keyword) {
        return factureRepository.searchByResident(resident, keyword.toLowerCase());
    }

}
