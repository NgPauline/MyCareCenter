package com.example.demo.service;

import com.example.demo.model.Consultation;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import com.example.demo.model.Activite;
import com.example.demo.model.HistoriqueConsultation;
import com.example.demo.repository.ConsultationRepository;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.HistoriqueConsultationRepository;

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
    private final HistoriqueConsultationRepository historiqueRepository;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ActiviteRepository activiteRepository,
                               HistoriqueConsultationRepository historiqueRepository) {
        this.consultationRepository = consultationRepository;
        this.activiteRepository = activiteRepository;
        this.historiqueRepository = historiqueRepository;
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

    public List<HistoriqueConsultation> findHistorique(Consultation consultation) {
        return historiqueRepository.findByConsultationOrderByDateModificationDesc(consultation);
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

    /* ------------------- UPDATE ------------------- */

    public void update(Integer id, Consultation updated) {

        Consultation original = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable"));

        /* Règle métier : consultation passée non modifiable */
        if (original.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Une consultation passée ne peut pas être modifiée.");
        }

        /* Règle métier : on ne change PAS le résident */
        updated.setResident(original.getResident());

        /* Règle métier : on ne change PAS le dossier médical */
        updated.setDossierMedical(original.getDossierMedical());

        /* Vérification disponibilité du soignant */
        Soignant soignant = updated.getSoignant();
        LocalDateTime date = updated.getDate();

        List<Activite> activitesSoignant = activiteRepository.findByResponsable(soignant);

        boolean chevaucheSoignant = activitesSoignant.stream().anyMatch(a -> {
            LocalDateTime debutAct = LocalDateTime.of(a.getDate(), a.getHeureDebut());
            LocalDateTime finAct = debutAct.plusMinutes(a.getDuree());
            return !date.isBefore(debutAct) && !date.isAfter(finAct);
        });

        if (chevaucheSoignant) {
            throw new IllegalArgumentException("Le soignant n'est pas disponible à cette date.");
        }

        /* Historique AVANT modification */
        HistoriqueConsultation hist = new HistoriqueConsultation(
                original,
                original.getDiagnostic(),
                original.getObservations(),
                updated.getSoignant() // idéalement : utilisateur connecté
        );
        historiqueRepository.save(hist);

        /* Mise à jour */
        original.setDate(updated.getDate());
        original.setDiagnostic(updated.getDiagnostic());
        original.setObservations(updated.getObservations());
        original.setSoignant(updated.getSoignant());

        consultationRepository.save(original);
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
