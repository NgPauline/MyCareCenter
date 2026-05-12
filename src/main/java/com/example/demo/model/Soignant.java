package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soignants")
public class Soignant extends Employe {

    private String diplome;
    private String specialite;

    @OneToMany(mappedBy = "soignant", fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private List<Activite> activites = new ArrayList<>();

    public Soignant() {
        super();
    }

    public Soignant(String nom, String prenom, LocalDate dateNaissance,
                    String telephone, String adresse,
                    String matricule, String poste, Double salaire,
                    String password, String roleApp,
                    String diplome, String specialite) {

        super(nom, prenom, dateNaissance, telephone, adresse,
              matricule, poste, salaire, password, roleApp);

        this.diplome = diplome;
        this.specialite = specialite;
    }

    // Getters & Setters

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }

    public List<Activite> getActivites() { return activites; }
    public void setActivites(List<Activite> activites) { this.activites = activites; }

    // Méthodes UML

    public void modifierDiplome(String diplome) { this.diplome = diplome; }

    public void modifierSpecialite(String specialite) { this.specialite = specialite; }

    public boolean verifierCompetence(String specialite) {
        return this.specialite != null && this.specialite.equalsIgnoreCase(specialite);
    }

    public void ajouterConsultation(Consultation consultation) {
        if (!consultations.contains(consultation)) {
            consultations.add(consultation);
            consultation.setSoignant(this);
        }
    }

    public List<Consultation> obtenirConsultations() {
        return consultations;
    }
}
