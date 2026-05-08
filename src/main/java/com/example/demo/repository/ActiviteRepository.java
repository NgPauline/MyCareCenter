package com.example.demo.repository;

import com.example.demo.model.Activite;
import com.example.demo.model.Resident;
  import com.example.demo.model.Employe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Integer> {

    List<Activite> findByDate(LocalDate date);

    List<Activite> findByParticipantsContaining(Resident resident);

    List<Activite> findByParticipants_IdPersonne(Integer idPersonne);


    List<Activite> findByResponsable(Employe responsable);

    long countByDate(LocalDate date);

    @Query("""
        SELECT a FROM Activite a
        WHERE LOWER(a.nom) LIKE %:kw%
            OR LOWER(CAST(a.categorie AS string)) LIKE %:kw%
            OR LOWER(a.lieu) LIKE %:kw%
            OR CAST(a.date AS string) LIKE %:kw%
            OR CAST(a.heureDebut AS string) LIKE %:kw%
            OR LOWER(a.responsable.nom) LIKE %:kw%
            OR LOWER(a.responsable.prenom) LIKE %:kw%
        """)
    List<Activite> search(@Param("kw") String keyword);

    @Query("""
        SELECT a FROM Activite a
        WHERE LOWER(a.nom) LIKE %:kw%
            OR LOWER(CAST(a.categorie AS string)) LIKE %:kw%
            OR LOWER(a.lieu) LIKE %:kw%
            OR CAST(a.date AS string) LIKE %:kw%
            OR CAST(a.heureDebut AS string) LIKE %:kw%
            OR LOWER(a.responsable.nom) LIKE %:kw%
            OR LOWER(a.responsable.prenom) LIKE %:kw%
        """)
    Page<Activite> search(@Param("kw") String keyword, Pageable pageable);
    
    }
