package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residents")
public class Resident extends Personne {

    @Column(name = "id_resident", unique = true, nullable = false)
    private Integer idResident;

    @Column(name = "typeHandicap", nullable = false)
    private String typeHandicap;

    @Column(nullable = false)
    private String niveauAutonomie;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate dateAdmission;

    @Column(nullable = false)
    private String statut;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resident_famille",
            joinColumns = @JoinColumn(name = "resident_id"),
            inverseJoinColumns = @JoinColumn(name = "famille_id"))
    private List<Famille> familles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DossierMedical dossierMedical;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Activite> activites = new ArrayList<>();

    @OneToMany(mappedBy = "resident", fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "resident", fetch = FetchType.LAZY)
    private List<Facture> factures = new ArrayList<>();


    // ============================
    //   CONSTRUCTEURS
    // ============================

    public Resident() {
        super();
    }

    // Ancien constructeur (avec idResident) — conservé si tu veux l’utiliser ailleurs
    public Resident(String nom, String prenom, LocalDate dateNaissance,
                    Integer idResident, String typeHandicap, String niveauAutonomie,
                    LocalDate dateAdmission, String statut) {
        super(nom, prenom, dateNaissance);
        this.idResident = idResident;
        this.typeHandicap = typeHandicap;
        this.niveauAutonomie = niveauAutonomie;
        this.dateAdmission = dateAdmission;
        this.statut = statut;
    }

    // NOUVEAU constructeur (sans idResident) — utilisé par ton DataInit et ton formulaire
    public Resident(String nom, String prenom, LocalDate dateNaissance,
                    String typeHandicap, String niveauAutonomie,
                    LocalDate dateAdmission, String statut) {
        super(nom, prenom, dateNaissance);
        this.typeHandicap = typeHandicap;
        this.niveauAutonomie = niveauAutonomie;
        this.dateAdmission = dateAdmission;
        this.statut = statut;
    }


    // ============================
    //   GETTERS / SETTERS
    // ============================

    public Integer getIdResident() {
        return idResident;
    }

    public void setIdResident(Integer idResident) {
        this.idResident = idResident;
    }

    public String getTypeHandicap() {
        return typeHandicap;
    }

    public void setTypeHandicap(String typeHandicap) {
        this.typeHandicap = typeHandicap;
    }

    public String getNiveauAutonomie() {
        return niveauAutonomie;
    }

    public void setNiveauAutonomie(String niveauAutonomie) {
        this.niveauAutonomie = niveauAutonomie;
    }

    public LocalDate getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(LocalDate dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
    }

    public List<Famille> getFamilles() {
        return familles;
    }

    public void setFamilles(List<Famille> familles) {
        this.familles = familles;
    }

    public DossierMedical getDossierMedical() {
        return dossierMedical;
    }

    public void setDossierMedical(DossierMedical dossierMedical) {
        this.dossierMedical = dossierMedical;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public List<Facture> getFactures() {
        return factures;
    }

    public void setFactures(List<Facture> factures) {
        this.factures = factures;
    }


    // ============================
    //   MÉTHODES UML
    // ============================

    public void ajouterFamille(Famille famille) {
        if (!familles.contains(famille)) {
            familles.add(famille);
            famille.getResidents().add(this);
        }
    }

    public int getAge() {
        if (getDateNaissance() == null) return 0;
        return Period.between(getDateNaissance(), LocalDate.now()).getYears();
    }

}
