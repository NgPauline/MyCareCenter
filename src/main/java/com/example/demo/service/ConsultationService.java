package com.example.demo.service;

import com.example.demo.model.Consultation;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import com.example.demo.model.Activite;
import com.example.demo.repository.ConsultationRepository;
import com.example.demo.repository.ActiviteRepository;

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

    /* ------------------- FIND ------------------- */

    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    public Optional<Consultation> findById(Integer idConsultation) {
        return consultationRepository.findById(idConsultation);
    }

    public List<Consultation> findByResident(Resident resident) {
        return consultationRepository.findByResident(resident);
    }

    public List<Consultation> findBySoignant(Soignant soignant) {
        return consultationRepository.findBySoignant(soignant);
    }

    /* ------------------- SAVE ------------------- */

    public Consultation save(Consultation consultation) {

        Resident resident = consultation.getResident();
        Soignant soignant = consultation.getSoignant();
        LocalDateTime date = consultation.getDate();

        /* Règle métier : consultation pas dans le futur */
        if (date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La consultation ne peut pas être dans le futur.");
        }

        /* Règle métier : dossier médical obligatoire */
        if (resident.getDossierMedical() == null) {
            throw new IllegalArgumentException("Le résident n'a pas de dossier médical.");
        }

        /* Vérifier chevauchement avec activités du résident */
        List<Activite> activitesResident = activiteRepository.findByParticipantsContaining(resident);

        boolean chevaucheResident = activitesResident.stream().anyMatch(a -> {
            LocalDateTime debutAct = LocalDateTime.of(a.getDate(), a.getHeureDebut());
            LocalDateTime finAct = debutAct.plusMinutes(a.getDuree());
            return !date.isBefore(debutAct) && !date.isAfter(finAct);
        });

        if (chevaucheResident) {
            throw new IllegalArgumentException("Chevauchement entre consultation et activité du résident.");
        }

        /* Vérifier chevauchement avec activités du soignant */
        List<Activite> activitesSoignant = activiteRepository.findByResponsable(soignant);

        boolean chevaucheSoignant = activitesSoignant.stream().anyMatch(a -> {
            LocalDateTime debutAct = LocalDateTime.of(a.getDate(), a.getHeureDebut());
            LocalDateTime finAct = debutAct.plusMinutes(a.getDuree());
            return !date.isBefore(debutAct) && !date.isAfter(finAct);
        });

        if (chevaucheSoignant) {
            throw new IllegalArgumentException("Le soignant n'est pas disponible à cette date.");
        }

        return consultationRepository.save(consultation);
    }

    /* ------------------- DELETE ------------------- */

    public void delete(Integer id) {

        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable"));

        /* Règle métier : consultation passée non supprimable */
        if (consultation.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Une consultation passée ne peut pas être supprimée.");
        }

        consultationRepository.deleteById(id);
    }

        public List<Consultation> searchByResident(Resident resident, String keyword) {
        return consultationRepository.searchByResident(resident, keyword.toLowerCase());
    }
}
