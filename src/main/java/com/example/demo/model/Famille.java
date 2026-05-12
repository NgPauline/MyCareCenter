package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "familles")
public class Famille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFamille;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String lienParente;

    private String type;

   @Pattern(
    regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$",
    message = "Numéro de téléphone européen invalide (ex: +32 475 12 34 56)"
    )
    @NotBlank(message = "Téléphone obligatoire")
    @Column(unique = true)
    private String telephone;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    @Column(unique = true)
    private String email;

    @Pattern(
    regexp = "^[A-Za-zÀ-ÿ'\\- ]+ \\d+[A-Za-z]? ?, ?\\d{4} [A-Za-zÀ-ÿ'\\- ]+$",
    message = "Adresse invalide (ex: Rue Exemple 12, 1000 Bruxelles)"
    )
    @NotBlank(message = "Adresse obligatoire")
    private String adresse;


    @ManyToMany(mappedBy = "familles", fetch = FetchType.LAZY)
    private List<Resident> residents = new ArrayList<>();

    public Famille() {
    }

    public Famille(String nom, String prenom, String lienParente, String type,
                   String telephone, String email, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.lienParente = lienParente;
        this.type = type;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    public Integer getIdFamille() {
        return idFamille;
    }

    public void setIdFamille(Integer idFamille) {
        this.idFamille = idFamille;
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

    public String getLienParente() {
        return lienParente;
    }

    public void setLienParente(String lienParente) {
        this.lienParente = lienParente;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    // UML : ajouterMembre
    public void ajouterMembre(String nom, String prenom, String lien) {
        this.nom = nom;
        this.prenom = prenom;
        this.lienParente = lien;
    }

    // UML : modifierContact
    public void modifierContact(String telephone, String email) {
        this.telephone = telephone;
        this.email = email;
    }

    // UML : obtenirResidents
    public List<Resident> obtenirResidents() {
        return residents;
    }
}
