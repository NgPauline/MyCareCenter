package com.example.demo.repository;

import com.example.demo.model.Employe;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    boolean existsByMatricule(String matricule);

    Employe findByMatricule(String matricule);

    boolean existsByRoleApp(String roleApp);
    List<Employe> findByRoleApp(String roleApp);

    long countByMatriculeStartingWith(String prefix);

    @Query("""
        SELECT e FROM Employe e
        WHERE LOWER(e.nom) LIKE %:kw%
            OR LOWER(e.prenom) LIKE %:kw%
            OR LOWER(e.poste) LIKE %:kw%
            OR LOWER(e.matricule) LIKE %:kw%
        """)
    List<Employe> search(@Param("kw") String keyword);

    @Query("""
        SELECT e FROM Employe e
        WHERE LOWER(e.nom) LIKE %:kw%
            OR LOWER(e.prenom) LIKE %:kw%
            OR LOWER(e.poste) LIKE %:kw%
            OR LOWER(e.matricule) LIKE %:kw%
        """)
    Page<Employe> search(@Param("kw") String keyword, Pageable pageable);

    List<Employe> findAllByOrderByNomAscPrenomAsc();
    Page<Employe> findAllByOrderByNomAscPrenomAsc(Pageable pageable);
}
