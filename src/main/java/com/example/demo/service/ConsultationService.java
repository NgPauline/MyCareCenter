package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Consultation;
import com.example.demo.model.Employe;
import com.example.demo.model.Resident;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ConsultationRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ActiviteRepository activiteRepository;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ActiviteRepository activiteRepository) {
        this.consultationRepository = consultationRepository;
        this.activiteRepository = activiteRepository;
    }

    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    public Optional<Consultation> findById(Integer idConsultation) {
        return consultationRepository.findById(idConsultation);
    }

    public List<Consultation> findByResident(Resident resident) {
        return consultationRepository.findByResident(resident);
    }

    public List<Consultation> findBySoignant(Employe soignant) {
        return consultationRepository.findBySoignant(soignant);
    }

    public Consultation save(Consultation consultation) {

        Resident resident = consultation.getResident();
        Employe soignant = consultation.getSoignant();
        LocalDateTime debut = consultation.getDate();
        LocalDateTime fin = consultation.getHeureFin();

        if (debut.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La consultation ne peut pas être dans le futur.");
        }

        if (resident.getDossierMedical() == null) {
            throw new IllegalArgumentException("Le résident n'a pas de dossier médical.");
        }

        /* Chevauchement avec activités du résident */
        List<Activite> activitesResident = activiteRepository.findByParticipantsContaining(resident);
        boolean chevaucheResident = activitesResident.stream().anyMatch(a -> {
            LocalDateTime debutAct = LocalDateTime.of(a.getDate(), a.getHeureDebut());
            LocalDateTime finAct = debutAct.plusMinutes(a.getDuree());
            return debut.isBefore(finAct) && fin.isAfter(debutAct);
        });
        if (chevaucheResident) {
            throw new IllegalArgumentException("Chevauchement entre la consultation et une activité du résident.");
        }

        /* Chevauchement avec activités du soignant */
        List<Activite> activitesSoignant = activiteRepository.findByResponsable(soignant);
        boolean chevaucheSoignant = activitesSoignant.stream().anyMatch(a -> {
            LocalDateTime debutAct = LocalDateTime.of(a.getDate(), a.getHeureDebut());
            LocalDateTime finAct = debutAct.plusMinutes(a.getDuree());
            return debut.isBefore(finAct) && fin.isAfter(debutAct);
        });
        if (chevaucheSoignant) {
            throw new IllegalArgumentException("Le soignant n'est pas disponible sur ce créneau.");
        }

        return consultationRepository.save(consultation);
    }

    public void delete(Integer id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable"));
        if (consultation.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Une consultation passée ne peut pas être supprimée.");
        }
        consultationRepository.deleteById(id);
    }

    public List<Consultation> searchByResident(Resident resident, String keyword) {
        return consultationRepository.searchByResident(resident, keyword.toLowerCase());
    }
}