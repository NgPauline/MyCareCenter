package com.example.demo.service;

import com.example.demo.model.Resident;
import com.example.demo.repository.ResidentRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ResidentService {

    private final ResidentRepository residentRepository;

    public ResidentService(ResidentRepository residentRepository) {
        this.residentRepository = residentRepository;
    }

    // -----------------------------
    // 1. Compter les résidents
    // -----------------------------
    public long count() {
        return residentRepository.count();
    }

    // -----------------------------
    // 2. Derniers résidents (Dashboard)
    // -----------------------------
    public List<Resident> findLast5() {
        return residentRepository.findTop5ByOrderByIdPersonneDesc();
    }

    // -----------------------------
    // 3. Trouver tous les résidents
    // -----------------------------
    public List<Resident> findAll() {
        return residentRepository.findAll();
    }

    public Page<Resident> findAll(Pageable pageable) {
        return residentRepository.findAll(pageable);
    }

    // -----------------------------
    // 4. Trouver par ID
    // -----------------------------
    public Optional<Resident> findById(Integer id) {
        return residentRepository.findById(id);
    }

    // -----------------------------
    // 5. Sauvegarder un résident
    // -----------------------------
    public Resident save(Resident resident) {

    if (resident.calculerAge() < 21) {
        throw new IllegalArgumentException("Le résident doit avoir au moins 21 ans");
    }

    // Génération automatique si idResident est null
    if (resident.getIdResident() == null) {
        Integer nextId = residentRepository.findMaxIdResident() + 1;
        resident.setIdResident(nextId);
    }

    return residentRepository.save(resident);
}


    // -----------------------------
    // 6. Mettre à jour un résident
    // -----------------------------
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

    // -----------------------------
    // 7. Résidents sans chambre
    // -----------------------------
    public List<Resident> findResidentsSansChambre() {
        return residentRepository.findByChambreIsNull();
    }


    public List<Resident> search(String keyword) {
    return residentRepository.search(keyword);
   }

   public Page<Resident> search(String keyword, Pageable pageable) {
    return residentRepository.search(keyword, pageable);
}

    // -----------------------------
    // 8. Supprimer un résident
    // -----------------------------
    public void delete(Integer id) {
        residentRepository.deleteById(id);
    }

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

public Resident createResident(Resident resident) {

    Integer nextId = residentRepository.findMaxIdResident() + 1;
    resident.setIdResident(nextId);

    return residentRepository.save(resident);
}


}
