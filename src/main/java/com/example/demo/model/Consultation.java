package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsultation;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String diagnostic;

    private String observations;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Soignant soignant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DossierMedical dossierMedical;

    public Consultation() {
    }

    public Consultation(LocalDateTime date, String diagnostic, String observations,
                        Resident resident, Soignant soignant, DossierMedical dossierMedical) {
        this.date = date;
        this.diagnostic = diagnostic;
        this.observations = observations;
        this.resident = resident;
        this.soignant = soignant;
        this.dossierMedical = dossierMedical;
    }

    public Integer getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Integer idConsultation) {
        this.idConsultation = idConsultation;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Soignant getSoignant() {
        return soignant;
    }

    public void setSoignant(Soignant soignant) {
        this.soignant = soignant;
    }

    public DossierMedical getDossierMedical() {
        return dossierMedical;
    }

    public void setDossierMedical(DossierMedical dossierMedical) {
        this.dossierMedical = dossierMedical;
    }

    // UML : ajouterObservation
    public void ajouterObservation(String observation) {
        if (this.observations == null || this.observations.isBlank()) {
            this.observations = observation;
        } else {
            this.observations += "\n" + observation;
        }
    }

    // UML : modifierDiagnostic
    public void modifierDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }
}