package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "types_equipement")
public class TypeEquipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;

    private String photoPath;

    // Quantité totale disponible dans le stock
    private int quantiteTotale;

    // --- Constructeurs ---

    public TypeEquipement() {
    }

    public TypeEquipement(String nom, String photoPath, int quantiteTotale) {
        this.nom = nom;
        this.photoPath = photoPath;
        this.quantiteTotale = quantiteTotale;
    }

    // --- Getters & Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getQuantiteTotale() {
        return quantiteTotale;
    }

    public void setQuantiteTotale(int quantiteTotale) {
        this.quantiteTotale = quantiteTotale;
    }
}
