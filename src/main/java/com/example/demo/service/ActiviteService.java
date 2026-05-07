package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Resident;
import com.example.demo.model.Consultation;
import com.example.demo.model.CategorieActivite;
import com.example.demo.model.Employe;
import com.example.demo.model.Soignant;

import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ConsultationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActiviteService {

    private final ActiviteRepository activiteRepository;
    private final ConsultationRepository consultationRepository;

    public ActiviteService(ActiviteRepository activiteRepository,
                           ConsultationRepository consultationRepository) {
        this.activiteRepository = activiteRepository;
        this.consultationRepository = consultationRepository;
    }

    public List<Activite> findAll() {
        return activiteRepository.findAll();
    }

    public Page<Activite> findAll(Pageable pageable) {
        return activiteRepository.findAll(pageable);
    }

    public Optional<Activite> findById(Integer id) {
        return activiteRepository.findById(id);
    }

    // 🔥 Détection des collisions d’horaires
    private boolean aCollisionActivite(Activite nouvelle, List<Activite> existantes) {

        LocalDate date = nouvelle.getDate();
        LocalTime debut = nouvelle.getHeureDebut();
        LocalTime fin = nouvelle.getHeureFin();

        for (Activite a : existantes) {

            if (nouvelle.getIdActivite() != null &&
                nouvelle.getIdActivite().equals(a.getIdActivite())) {
                continue;
            }

            if (!a.getDate().equals(date)) continue;

            boolean chevauche =
                    debut.isBefore(a.getHeureFin()) &&
                    fin.isAfter(a.getHeureDebut());

            if (chevauche) return true;
        }

        return false;
    }

    public Activite save(Activite activite, Employe createur) {
        // 🔒 ÉDUCATEUR → interdit de créer du médical
        if ("EDUCATEUR".equals(createur.getRoleApp()) &&
            activite.getCategorie() == CategorieActivite.MEDICAL) {
            throw new IllegalArgumentException("Un éducateur ne peut pas créer une activité médicale.");
        }

        // 🔒 SOIGNANT → interdit de créer éducatif / sportif
        if (createur instanceof Soignant &&
            (activite.getCategorie() == CategorieActivite.EDUCATIF
            || activite.getCategorie() == CategorieActivite.SPORTIF)) {
            throw new IllegalArgumentException("Un soignant ne peut pas créer une activité éducative ou sportive.");
        }

        // 🔥 Collision horaire (ta logique existante)
        List<Activite> existantes = activiteRepository.findByDate(activite.getDate());
        if (aCollisionActivite(activite, existantes)) {
            throw new IllegalArgumentException("Collision détectée : l’horaire chevauche une autre activité.");
        }

        return activiteRepository.save(activite);
    }


    public void update(Integer id, Activite updated, Employe createur) {
        // 🔒 ÉDUCATEUR → interdit de modifier du médical
        if ("EDUCATEUR".equals(createur.getRoleApp()) &&
            updated.getCategorie() == CategorieActivite.MEDICAL) {
            throw new IllegalArgumentException("Un éducateur ne peut pas modifier une activité médicale.");
        }

        // 🔒 SOIGNANT → interdit de modifier éducatif / sportif
        if (createur instanceof Soignant &&
            (updated.getCategorie() == CategorieActivite.EDUCATIF
            || updated.getCategorie() == CategorieActivite.SPORTIF)) {
            throw new IllegalArgumentException("Un soignant ne peut pas modifier une activité éducative ou sportive.");
        }

        // 🔥 Collision horaire
        List<Activite> existantes = activiteRepository.findByDate(updated.getDate());
        if (aCollisionActivite(updated, existantes)) {
            throw new IllegalArgumentException("Collision détectée : l’horaire chevauche une autre activité.");
        }

        Activite original = activiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activité introuvable"));

        original.setNom(updated.getNom());
        original.setDate(updated.getDate());
        original.setDuree(updated.getDuree());
        original.setLieu(updated.getLieu());
        original.setHeureDebut(updated.getHeureDebut());
        original.setResponsable(updated.getResponsable());
        original.setCategorie(updated.getCategorie());

        activiteRepository.save(original);
    }


    public void delete(Integer id) {
        activiteRepository.deleteById(id);
    }

    public boolean isActivitePassee(Integer id) {
        Activite act = activiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activité introuvable"));

        return act.getDate().isBefore(LocalDate.now());
    }

    public void inscrireResident(Activite activite, Resident resident) {

        LocalDateTime debutAct = LocalDateTime.of(
                activite.getDate(),
                activite.getHeureDebut()
        );
        LocalDateTime finAct = debutAct.plusMinutes(activite.getDuree());

        List<Consultation> consults = consultationRepository
                .findByResidentAndDateBetween(resident,
                        debutAct.minusHours(4),
                        finAct.plusHours(4));

        boolean chevauche = consults.stream().anyMatch(c ->
                !c.getDate().isBefore(debutAct) && !c.getDate().isAfter(finAct));

        if (chevauche) {
            throw new IllegalArgumentException("Chevauchement entre activité et consultation");
        }

        activite.ajouterParticipant(resident);
        activiteRepository.save(activite);
    }

    public void desinscrireResident(Activite activite, Resident resident) {
        activite.retirerParticipant(resident);
        activiteRepository.save(activite);
    }

    public List<Activite> findByResident(Resident resident) {
        return activiteRepository.findByParticipantsContaining(resident);
    }

    public long countToday() {
        return activiteRepository.countByDate(LocalDate.now());
    }

    public List<Activite> findByParticipant(Integer idResident) {
        return activiteRepository.findByParticipants_IdPersonne(idResident);
    }

    public List<Activite> search(String keyword) {
        return activiteRepository.search(keyword.toLowerCase());
    }

    public Page<Activite> search(String keyword, Pageable pageable) {
        return activiteRepository.search(keyword.toLowerCase(), pageable);
    }

    }

