package com.example.demo.model;

import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Administratif extends Employe {

    private String fonction;

    public Administratif() {
        super();
    }

    public Administratif(String nom, String prenom, LocalDate dateNaissance,
                         String telephone, String adresse,
                         String matricule, String poste, Double salaire,
                         String password, String roleApp,
                         String fonction) {

        super(nom, prenom, dateNaissance,
              telephone, adresse,
              matricule, poste, salaire,
              password, roleApp);

        this.fonction = fonction;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    // UML : genererFacture
    public Facture genererFacture(Resident resident, double montant) {
        Facture facture = new Facture();
        facture.setResident(resident);
        facture.setMontant(montant);
        facture.setStatut("EN_ATTENTE");
        facture.setDateEmission(java.time.LocalDate.now());
        return facture;
    }

    // UML : traiterPaiement
    public void traiterPaiement(Paiement paiement) {
        paiement.validerPaiement();
    }

    // UML : obtenirFacturesEnAttente
    public List<Facture> obtenirFacturesEnAttente(List<Facture> toutes) {
        List<Facture> res = new ArrayList<>();
        for (Facture f : toutes) {
            if ("EN_ATTENTE".equalsIgnoreCase(f.getStatut())) {
                res.add(f);
            }
        }
        return res;
    }
}
