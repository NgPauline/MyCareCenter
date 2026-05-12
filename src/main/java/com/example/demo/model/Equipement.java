package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "equipements",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type_id", "chambre_id"})
    }
)
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipement;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private TypeEquipement type;

    @Column(nullable = false)
    private String etat;

    private String photoPath;

    @ManyToOne
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;

    // --- Constructeurs ---

    public Equipement() {
    }

    public Equipement(TypeEquipement type, String etat, String photoPath, Chambre chambre) {
        this.type = type;
        this.etat = etat;
        this.photoPath = photoPath;
        this.chambre = chambre;
    }

    // --- Getters & Setters ---

    public Integer getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(Integer idEquipement) {
        this.idEquipement = idEquipement;
    }

    public TypeEquipement getType() {
        return type;
    }

    public void setType(TypeEquipement type) {
        this.type = type;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
    }
}
