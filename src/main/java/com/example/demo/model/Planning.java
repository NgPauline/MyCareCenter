package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "plannings")
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlanning;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;
    
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Employe responsable;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "planning_activite",
            joinColumns = @JoinColumn(name = "planning_id"),
            inverseJoinColumns = @JoinColumn(name = "activite_id"))
    private List<Activite> activites = new ArrayList<>();

    public Planning() {
    }

    public Planning(LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public Integer getIdPlanning() {
        return idPlanning;
    }

    public void setIdPlanning(Integer idPlanning) {
        this.idPlanning = idPlanning;
    }

    public Employe getResponsable() {
    return responsable;
    }

    public void setResponsable(Employe responsable) {
        this.responsable = responsable;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    // UML : ajouterActivite
    public void ajouterActivite(Activite activite) {
        if (!activites.contains(activite)) {
            activites.add(activite);
        }
    }

    // UML : supprimerActivite
    public void supprimerActivite(int idActivite) {
        activites.removeIf(a -> a.getIdActivite() != null && a.getIdActivite() == idActivite);
    }

    // UML : obtenirActivitesDuJour
    public List<Activite> obtenirActivitesDuJour(LocalDate date) {
        if (this.date != null && this.date.equals(date)) {
            return activites;
        }
        return List.of();
    }

    // UML : calculerNombreHeures
    public double calculerNombreHeures() {
        if (heureDebut == null || heureFin == null) return 0;
        return Duration.between(heureDebut, heureFin).toMinutes() / 60.0;
    }

    @AssertTrue(message = "L'heure de début doit être avant l'heure de fin")
    public boolean isHeureValide() {
        if (heureDebut == null || heureFin == null) return true;
        return heureDebut.isBefore(heureFin);
    }

}

