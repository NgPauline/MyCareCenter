package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dossiers_medicaux")
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDossier;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private String groupeSanguin;

    private String allergies;

    private String observationsGenerales;

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Traitement> traitements = new ArrayList<>();

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToOne(mappedBy = "dossierMedical", optional = true)
    private Resident resident;

    public DossierMedical() {
    }

    public DossierMedical(LocalDate dateCreation, String groupeSanguin,
                          String allergies, String observationsGenerales) {
        this.dateCreation = dateCreation;
        this.groupeSanguin = groupeSanguin;
        this.allergies = allergies;
        this.observationsGenerales = observationsGenerales;
    }

    public Integer getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(Integer idDossier) {
        this.idDossier = idDossier;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getObservationsGenerales() {
        return observationsGenerales;
    }

    public void setObservationsGenerales(String observationsGenerales) {
        this.observationsGenerales = observationsGenerales;
    }

    public List<Traitement> getTraitements() {
        return traitements;
    }

    public void setTraitements(List<Traitement> traitements) {
        this.traitements = traitements;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public Resident getResident() {
         return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    // UML : ajouterTraitement
    public void ajouterTraitement(Traitement traitement) {
        if (!traitements.contains(traitement)) {
            traitements.add(traitement);
            traitement.setDossierMedical(this);
        }
    }

    // UML : supprimerTraitement
    public void supprimerTraitement(int idTraitement) {
        traitements.removeIf(t -> t.getIdTraitement() != null && t.getIdTraitement() == idTraitement);
    }

    // UML : ajouterConsultation
    public void ajouterConsultation(Consultation consultation) {
        if (!consultations.contains(consultation)) {
            consultations.add(consultation);
            consultation.setDossierMedical(this);
        }
    }

    // UML : obtenirTraitementsActifs
    public List<Traitement> obtenirTraitementsActifs() {
        LocalDate today = LocalDate.now();
        return traitements.stream()
                .filter(t -> t.verifierValidite(today))
                .toList();
    }

    // UML : obtenirHistoriqueConsultations
    public List<Consultation> obtenirHistoriqueConsultations() {
        return consultations;
    }

    // UML : modifierAllergies
    public void modifierAllergies(String allergies) {
        this.allergies = allergies;
    }

    // UML : modifierObservations
    public void modifierObservations(String observations) {
        this.observationsGenerales = observations;
    }
}
