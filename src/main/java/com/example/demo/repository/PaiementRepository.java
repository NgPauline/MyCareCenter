package com.example.demo.repository;

import com.example.demo.model.Paiement;
import com.example.demo.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {

    List<Paiement> findByFacture(Facture facture);
    
    List<Paiement> findByFacture_Resident_IdPersonne(Integer idPersonne);

}
