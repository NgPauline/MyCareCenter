package com.example.demo.repository;

import com.example.demo.model.Consultation;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

    List<Consultation> findByResident(Resident resident);

    List<Consultation> findBySoignant(Soignant soignant);

    List<Consultation> findByDateBetween(LocalDateTime debut, LocalDateTime fin);

    List<Consultation> findByResidentAndDateBetween(Resident resident, LocalDateTime debut, LocalDateTime fin);

        @Query("""
        SELECT c FROM Consultation c
        WHERE c.resident = :resident
        AND (
            LOWER(c.diagnostic) LIKE %:kw%
            OR LOWER(c.observations) LIKE %:kw%
            OR LOWER(c.soignant.nom) LIKE %:kw%
            OR LOWER(c.soignant.prenom) LIKE %:kw%
        )
        """)
    List<Consultation> searchByResident(@Param("resident") Resident resident,
                                        @Param("kw") String keyword);
}
