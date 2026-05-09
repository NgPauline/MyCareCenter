package com.example.demo.repository;

import com.example.demo.model.Famille;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FamilleRepository extends JpaRepository<Famille, Integer> {
    List<Famille> findByResidents_IdPersonne(Integer idPersonne);

    @Query("""
    SELECT f FROM Famille f
    JOIN f.residents r
    WHERE r.idPersonne = :idResident
    AND (
        LOWER(f.nom) LIKE %:kw%
        OR LOWER(f.prenom) LIKE %:kw%
        OR LOWER(f.lienParente) LIKE %:kw%
        OR LOWER(f.telephone) LIKE %:kw%
    )
    """)
List<Famille> searchByResidentId(@Param("idResident") Integer idResident,
                                 @Param("kw") String keyword);

}
