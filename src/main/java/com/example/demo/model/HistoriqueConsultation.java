package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoriqueConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorique;

    @Column(nullable = false)
    private LocalDateTime dateModification;

    private String ancienDiagnostic;

    private String anciennesObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Soignant modifiePar;

    public HistoriqueConsultation() {}

    public HistoriqueConsultation(Consultation consultation,
                                  String ancienDiagnostic,
                                  String anciennesObservations,
                                  Soignant modifiePar) {
        this.consultation = consultation;
        this.ancienDiagnostic = ancienDiagnostic;
        this.anciennesObservations = anciennesObservations;
        this.modifiePar = modifiePar;
        this.dateModification = LocalDateTime.now();
    }

    public Integer getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(Integer idHistorique) {
        this.idHistorique = idHistorique;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public String getAncienDiagnostic() {
        return ancienDiagnostic;
    }

    public void setAncienDiagnostic(String ancienDiagnostic) {
        this.ancienDiagnostic = ancienDiagnostic;
    }

    public String getAnciennesObservations() {
        return anciennesObservations;
    }

    public void setAnciennesObservations(String anciennesObservations) {
        this.anciennesObservations = anciennesObservations;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Soignant getModifiePar() {
        return modifiePar;
    }

    public void setModifiePar(Soignant modifiePar) {
        this.modifiePar = modifiePar;
    }
}
