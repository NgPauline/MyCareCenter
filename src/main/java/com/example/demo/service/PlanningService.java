package com.example.demo.service;

import com.example.demo.dto.PlanningCalendarDTO;
import com.example.demo.model.Planning;
import com.example.demo.model.Activite;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.PlanningRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ActiviteRepository activiteRepository;

    public PlanningService(PlanningRepository planningRepository, ActiviteRepository activiteRepository) {
        this.planningRepository = planningRepository;
        this.activiteRepository = activiteRepository;
    }

    // ---------------------------------------------------------
    // PALETTE PREMIUM POUR LES SOIGNANTS
    // ---------------------------------------------------------

    private static final String[] SOIGNANT_COLORS = {
            "#D4AF37", // Gold premium
            "#4A90E2", // Bleu doux
            "#50E3C2", // Menthe
            "#F5A623", // Orange
            "#BD10E0", // Violet
            "#7ED321", // Vert clair
            "#B8E986"  // Vert pastel
    };

    private String getColorForSoignant(Integer soignantId) {
        if (soignantId == null) return "#999999"; // neutre
        return SOIGNANT_COLORS[soignantId % SOIGNANT_COLORS.length];
    }

    // ---------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------

    public List<Planning> findAll() {
        return planningRepository.findAll();
    }

    public Page<Planning> findAll(Pageable pageable) {
        return planningRepository.findAll(pageable);
    }

    public Optional<Planning> findById(Integer id) {
        return planningRepository.findById(id);
    }

    public Planning save(Planning planning) {

    // ✅ Vérifier qu'un employé n'a pas déjà un créneau qui chevauche
        List<Planning> existants = planningRepository.findByDate(planning.getDate());

        boolean chevauche = existants.stream()
            .filter(p -> p.getResponsable().getIdPersonne()
                        .equals(planning.getResponsable().getIdPersonne()))
            .anyMatch(p ->
                planning.getHeureDebut().isBefore(p.getHeureFin()) &&
                planning.getHeureFin().isAfter(p.getHeureDebut())
            );

        if (chevauche) {
            throw new IllegalArgumentException(
                "Cet employé a déjà un créneau qui chevauche cet horaire."
            );
        }

        return planningRepository.save(planning);
    }

    public void update(Integer id, Planning updated) {
        Planning original = planningRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planning introuvable"));

        original.setDate(updated.getDate());
        original.setHeureDebut(updated.getHeureDebut());
        original.setHeureFin(updated.getHeureFin());
        original.setResponsable(updated.getResponsable());
        original.setActivites(updated.getActivites());

        planningRepository.save(original);
    }

    public void ajouterActivite(Planning planning, Activite activite) {
        planning.ajouterActivite(activite);
        planningRepository.save(planning);
    }

    public void delete(Integer id) {
        planningRepository.deleteById(id);
    }

    // ---------------------------------------------------------
    // JSON POUR FULLCALENDAR (DTO)
    // ---------------------------------------------------------

    public String getPlanningsAsJson() {
        try {
            List<Planning> plannings = planningRepository.findAll();

            List<PlanningCalendarDTO> events = plannings.stream()
                    .map(p -> new PlanningCalendarDTO(
                            p.getIdPlanning(),
                            p.getResponsable().getNom() + " " + p.getResponsable().getPrenom(),
                            p.getDate().toString() + "T" + p.getHeureDebut().toString(),
                            p.getDate().toString() + "T" + p.getHeureFin().toString(),
                            p.getResponsable().getIdPersonne(),
                            p.getActivites().isEmpty() ? null : p.getActivites().get(0).getNom(),
                            getColorForSoignant(p.getResponsable().getIdPersonne())
                    ))
                    .toList();

            return objectMapper.writeValueAsString(events);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur JSON dans getPlanningsAsJson()", e);
        }
    }

    public List<Planning> search(String keyword) {
        return planningRepository.search(keyword.toLowerCase());
    }

    public Page<Planning> search(String keyword, Pageable pageable) {
        return planningRepository.search(keyword.toLowerCase(), pageable);

    }

    public List<Activite> findActivitesByDate(LocalDate date) {
    return activiteRepository.findAll().stream()
        .filter(a -> date.equals(a.getDate()))
        .collect(Collectors.toList());
}
}