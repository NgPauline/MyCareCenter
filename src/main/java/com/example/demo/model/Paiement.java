package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaiement;

    @NotNull
    private LocalDate datePaiement;
    
    @DecimalMin("0.0")
    private double montant;

    @NotBlank
    private String mode;
    
    private boolean valide;

    @ManyToOne(fetch = FetchType.LAZY)
    private Facture facture;

    public Paiement() {
    }

    public Paiement(LocalDate datePaiement, double montant, String mode, Facture facture) {
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.mode = mode;
        this.facture = facture;
        this.valide = false;
    }

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    // UML : validerPaiement
    public boolean validerPaiement() {
        this.valide = true;
        return true;
    }

    // UML : annulerPaiement
    public void annulerPaiement() {
        this.valide = false;
    }

    // UML : obtenirFacture
    public Facture obtenirFacture() {
        return facture;
    }
}
