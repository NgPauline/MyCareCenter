package com.example.demo.repository;

import com.example.demo.model.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PlanningRepository extends JpaRepository<Planning, Integer> {

    List<Planning> findByDate(LocalDate date);

     boolean existsByResponsable_IdPersonne(Integer id);

        @Query("""
        SELECT p FROM Planning p
        LEFT JOIN p.activites a
        WHERE CAST(p.date AS string) LIKE %:kw%
            OR CAST(p.heureDebut AS string) LIKE %:kw%
            OR CAST(p.heureFin AS string) LIKE %:kw%
            OR LOWER(p.responsable.nom) LIKE %:kw%
            OR LOWER(p.responsable.prenom) LIKE %:kw%
            OR LOWER(a.nom) LIKE %:kw%
        """)
    List<Planning> search(@Param("kw") String keyword);

        @Query("""
        SELECT DISTINCT p FROM Planning p
        LEFT JOIN p.activites a
        WHERE CAST(p.date AS string) LIKE %:kw%
            OR CAST(p.heureDebut AS string) LIKE %:kw%
            OR CAST(p.heureFin AS string) LIKE %:kw%
            OR LOWER(p.responsable.nom) LIKE %:kw%
            OR LOWER(p.responsable.prenom) LIKE %:kw%
            OR LOWER(a.nom) LIKE %:kw%
        """)
    Page<Planning> search(@Param("kw") String keyword, Pageable pageable);

}
