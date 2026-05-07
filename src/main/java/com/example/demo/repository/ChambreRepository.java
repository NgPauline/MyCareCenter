package com.example.demo.repository;

import com.example.demo.model.Chambre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChambreRepository extends JpaRepository<Chambre, Integer> {

    boolean existsByNumero(String numero);

    Chambre findByNumero(String numero);

    // Chambres libres (champ = occupant)
    List<Chambre> findByOccupantIsNull();

        @Query("""
        SELECT c FROM Chambre c
        WHERE LOWER(c.numero) LIKE %:kw%
            OR LOWER(c.type) LIKE %:kw%
            OR CAST(c.etage AS string) LIKE %:kw%
            OR (c.occupant IS NULL AND :kw = 'libre')
            OR (c.occupant IS NOT NULL AND :kw = 'occupee')
        """)
    List<Chambre> search(@Param("kw") String keyword);

        @Query("""
        SELECT c FROM Chambre c
        WHERE LOWER(c.numero) LIKE %:kw%
            OR LOWER(c.type) LIKE %:kw%
            OR CAST(c.etage AS string) LIKE %:kw%
            OR (c.occupant IS NULL AND :kw = 'libre')
            OR (c.occupant IS NOT NULL AND :kw = 'occupee')
        """)
    Page<Chambre> search(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(c.numero, 3) AS int)), 0) FROM Chambre c")
    Integer findMaxNumero();



}

