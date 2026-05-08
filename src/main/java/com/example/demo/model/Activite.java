package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.model.Activite;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idActivite;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private int duree; // minutes

    private String lieu;

    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieActivite categorie;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "activite_resident",
            joinColumns = @JoinColumn(name = "activite_id"),
            inverseJoinColumns = @JoinColumn(name = "resident_id"))
    private List<Resident> participants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Employe responsable;

    public Activite() {}

    public Activite(LocalDate date, int duree, String lieu, LocalTime heureDebut) {
        this.date = date;
        this.duree = duree;
        this.lieu = lieu;
        this.heureDebut = heureDebut;
    }

    public Integer getIdActivite() { return idActivite; }
    public void setIdActivite(Integer idActivite) { this.idActivite = idActivite; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public CategorieActivite getCategorie() {
    return categorie;
    }

    public void setCategorie(CategorieActivite categorie) {
        this.categorie = categorie;
    }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public List<Resident> getParticipants() { return participants; }
    public void setParticipants(List<Resident> participants) { this.participants = participants; }

    public Employe getResponsable() { return responsable; }
    public void setResponsable(Employe responsable) { this.responsable = responsable; }

    // 🔥 Heure de fin calculée automatiquement
    @Transient
    public LocalTime getHeureFin() {
        return heureDebut != null ? heureDebut.plusMinutes(duree) : null;
    }

    public void ajouterParticipant(Resident resident) {
        if (!participants.contains(resident)) {
            participants.add(resident);
            resident.getActivites().add(this);
        }
    }

    public void retirerParticipant(Resident resident) {
        if (participants.remove(resident)) {
            resident.getActivites().remove(this);
        }
    }

    public int obtenirNombreParticipants() {
        return participants.size();
    }

    public List<Resident> obtenirListeParticipants() {
        return participants;
    }

    // @AssertTrue(message = "Les activités doivent être planifiées au moins 7 jours à l'avance")
    public boolean isPlanifieeAssezTot() {
        if (date == null) return true;
        return !date.isBefore(LocalDate.now().plusDays(7));
    }
}
