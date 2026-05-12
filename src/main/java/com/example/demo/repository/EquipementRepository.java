package com.example.demo.repository;

import com.example.demo.model.Chambre;
import com.example.demo.model.Equipement;
import com.example.demo.model.TypeEquipement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {

    List<Equipement> findByChambre_IdChambre(Integer idChambre);

    // -----------------------------
    // SEARCH (liste simple)
    // -----------------------------
    @Query("""
           SELECT e FROM Equipement e
           WHERE LOWER(e.type.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.etat) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.chambre.numero) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    List<Equipement> search(@Param("keyword") String keyword);

    // -----------------------------
    // SEARCH (pagination)
    // -----------------------------
    @Query("""
           SELECT e FROM Equipement e
           WHERE LOWER(e.type.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.etat) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.chambre.numero) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Equipement> search(@Param("keyword") String keyword, Pageable pageable);

    // -----------------------------
    // COUNT
    // -----------------------------
    long countByChambreIsNotNull();

    // -----------------------------
    // CONTRAINTE : un type par chambre
    // -----------------------------
    Optional<Equipement> findByChambreAndType(Chambre chambre, TypeEquipement type);

    long countByType_Id(Integer typeId);

    List<Equipement> findAllByOrderByType_NomAsc();
    Page<Equipement> findAllByOrderByType_NomAsc(Pageable pageable);

}
