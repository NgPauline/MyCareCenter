package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
public abstract class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersonne;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotNull
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    public Personne() {
    }

    public Personne(String nom, String prenom, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public Integer getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Integer idPersonne) {
        this.idPersonne = idPersonne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    @Transient
    public int calculerAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    @AssertTrue(message = "La personne doit avoir entre 21 et 60 ans")
    public boolean isTrancheAgeValide() {
        int age = calculerAge();
        return age >= 21 && age <= 60;
    }


    @Override
    public String toString() {
        return nom + " " + prenom + " (" + calculerAge() + " ans)";
    }
}
