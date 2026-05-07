package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class Chambre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idChambre;

    @Pattern(
        regexp = "^CH\\d{3}$",
        message = "Numéro de chambre invalide (ex: CH001)"
    )
    @Column(unique = true, nullable = false)
    private String numero;


    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int etage;

    @OneToOne(mappedBy = "chambre", fetch = FetchType.LAZY)
    private Resident occupant;
    
    @OneToMany(mappedBy = "chambre", fetch = FetchType.LAZY)
     private List<Equipement> equipements = new ArrayList<>();


    public Chambre() {
    }

    public Chambre(String numero, String type, int etage) {
        this.numero = numero;
        this.type = type;
        this.etage = etage;
    }

    public Integer getIdChambre() {
        return idChambre;
    }

    public void setIdChambre(Integer idChambre) {
        this.idChambre = idChambre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public Resident getOccupant() {
        return occupant;
    }

    public void setOccupant(Resident occupant) {
        this.occupant = occupant;
    }

    // UML : verifierDisponibilite
    public boolean verifierDisponibilite() {
        return occupant == null;
    }

    // UML : modifierType
    public void modifierType(String type) {
        this.type = type;
    }
}
