package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Traitement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTraitement;

    @NotNull
    private String medicament;

    @NotNull
    private String dosage;

    @NotNull
    private String frequence;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    private DossierMedical dossierMedical;

    public Traitement() {}

    public Traitement(String medicament, String dosage, String frequence,
                      LocalDate dateDebut, LocalDate dateFin) {
        this.medicament = medicament;
        this.dosage = dosage;
        this.frequence = frequence;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Integer getIdTraitement() {
        return idTraitement;
    }

    public void setIdTraitement(Integer idTraitement) {
        this.idTraitement = idTraitement;
    }

    public String getMedicament() {
        return medicament;
    }

    public void setMedicament(String medicament) {
        this.medicament = medicament;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public DossierMedical getDossierMedical() {
        return dossierMedical;
    }

    public void setDossierMedical(DossierMedical dossierMedical) {
        this.dossierMedical = dossierMedical;
    }

    /* ---------------- MÉTHODES UML ---------------- */

    public boolean verifierValidite(LocalDate dateActuelle) {
        boolean apresDebut = (dateDebut == null) || !dateActuelle.isBefore(dateDebut);
        boolean avantFin = (dateFin == null) || !dateActuelle.isAfter(dateFin);
        return apresDebut && avantFin;
    }

    public boolean isActif() {
        return verifierValidite(LocalDate.now());
    }

    public boolean getActif() {
        return isActif();
    }

    public void modifierDosage(String dosage) {
        this.dosage = dosage;
    }

    public void modifierFrequence(String frequence) {
        this.frequence = frequence;
    }

    public int calculerDuree() {
        if (dateDebut == null || dateFin == null) return 0;
        return (int) ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    @AssertTrue(message = "La date de fin doit être postérieure ou égale à la date de début")
    public boolean isDatesValides() {
        if (dateDebut == null || dateFin == null) return true;
        return !dateFin.isBefore(dateDebut);
    }

}
