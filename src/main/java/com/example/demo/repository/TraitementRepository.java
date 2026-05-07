package com.example.demo.repository;

import com.example.demo.model.Traitement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TraitementRepository extends JpaRepository<Traitement, Integer> {

    @Query("""
           SELECT t
           FROM Traitement t
           JOIN FETCH t.dossierMedical dm
           JOIN FETCH dm.resident
           """)
    List<Traitement> findAllWithResident();

    List<Traitement> findByDossierMedical_IdDossier(Integer idDossier);
}
