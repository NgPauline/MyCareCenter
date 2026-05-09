package com.example.demo.repository;

import com.example.demo.model.Resident;
import com.example.demo.model.Traitement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TraitementRepository extends JpaRepository<Traitement, Integer> {

    @Query("""
           SELECT t
           FROM Traitement t
           JOIN FETCH t.dossierMedical dm
           JOIN FETCH dm.resident
           """)
    List<Traitement> findAllWithResident();

    List<Traitement> findByDossierMedical_IdDossier(Integer idDossier);

        @Query("""
        SELECT t FROM Traitement t
        JOIN FETCH t.dossierMedical dm
        JOIN FETCH dm.resident r
        WHERE dm.resident = :resident
        AND (
            LOWER(t.medicament) LIKE %:kw%
            OR LOWER(t.frequence) LIKE %:kw%
            OR LOWER(t.dosage) LIKE %:kw%
        )
        """)
    List<Traitement> searchByResident(@Param("resident") Resident resident,
                                    @Param("kw") String keyword);
}
