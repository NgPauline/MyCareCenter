package com.example.demo.repository;

import com.example.demo.model.Paiement;
import com.example.demo.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {

    List<Paiement> findByFacture(Facture facture);
    
    List<Paiement> findByFacture_Resident_IdPersonne(Integer idPersonne);

        @Query("""
        SELECT p FROM Paiement p
        WHERE p.facture.resident.idPersonne = :idResident
        AND (
            LOWER(p.mode) LIKE %:kw%
            OR LOWER(p.facture.resident.nom) LIKE %:kw%
            OR LOWER(p.facture.resident.prenom) LIKE %:kw%
        )
        """)
    List<Paiement> searchByResidentId(@Param("idResident") Integer idResident,
                                    @Param("kw") String keyword);

}
