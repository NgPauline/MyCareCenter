package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Employe extends Personne {

    @Pattern(
        regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$",
        message = "Numéro de téléphone européen invalide (ex: +32 475 12 34 56)"
    )
    @NotBlank(message = "Téléphone obligatoire")
    private String telephone;

   @Pattern(
        regexp = "^[A-Za-zÀ-ÿ'\\- ]+ \\d+[A-Za-z]? ?, ?\\d{4} [A-Za-zÀ-ÿ'\\- ]+$",
        message = "Adresse invalide (ex: Rue Exemple 12, 1000 Bruxelles)"
    )
    @NotBlank(message = "Adresse obligatoire")
    private String adresse;


    @NotBlank
    private String password;

    @Pattern(
        regexp = "^(DIR|EMP|SOI|EDU|FIN)\\d{3}$",
        message = "Matricule invalide (ex: DIR001, EMP002, SOI003)"
    )
    @Column(unique = true, nullable = false)
    private String matricule;


    @Column(nullable = false)
    private String poste;

    @NotNull
    private Double salaire;

    @Column(nullable = false)
    private String roleApp; // DIRECTEUR, ADMINISTRATIF, SOIGNANT, EDUCATEUR, FINANCE

    public Employe() {
        super();
    }

    public Employe(String nom, String prenom, LocalDate dateNaissance,
                   String telephone, String adresse,
                   String matricule, String poste, Double salaire,
                   String password, String roleApp) {

        super(nom, prenom, dateNaissance);
        this.telephone = telephone;
        this.adresse = adresse;
        this.matricule = matricule;
        this.poste = poste;
        this.salaire = salaire;
        this.password = password;
        this.roleApp = roleApp;
    }

    // Getters & Setters

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public Double getSalaire() { return salaire; }
    public void setSalaire(Double salaire) { this.salaire = salaire; }

    public String getRoleApp() { return roleApp; }
    public void setRoleApp(String roleApp) { this.roleApp = roleApp; }

    @AssertTrue(message = "Le salaire doit être strictement supérieur à 0")
    public boolean isSalaireValide() {
        return salaire != null && salaire > 0;
}

}
