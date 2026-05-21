package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsultation;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime date;

    private int duree;

    @Column(nullable = false)
    private String diagnostic;

    private String observations;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Employe soignant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DossierMedical dossierMedical;


    public Consultation() {
    }

    public Consultation(LocalDateTime date, int duree, String diagnostic, String observations,
                    Resident resident, Employe soignant, DossierMedical dossierMedical) {
    this.date = date;
    this.duree = duree;
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

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; 
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Employe getSoignant() {
        return soignant;
    }

    public void setSoignant(Employe soignant) {
        this.soignant = soignant;
    }

    public DossierMedical getDossierMedical() {
        return dossierMedical;
    }

    public void setDossierMedical(DossierMedical dossierMedical) {
        this.dossierMedical = dossierMedical;
    }

    @Transient
    public LocalDateTime getHeureFin() {
        return date != null ? date.plusMinutes(duree) : null;
    }

}