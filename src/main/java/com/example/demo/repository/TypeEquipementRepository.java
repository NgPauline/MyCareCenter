package com.example.demo.repository;

import com.example.demo.model.TypeEquipement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TypeEquipementRepository extends JpaRepository<TypeEquipement, Integer> {

    @Query("SELECT t FROM TypeEquipement t WHERE LOWER(t.nom) LIKE %:kw%")
    List<TypeEquipement> search(@Param("kw") String keyword);

    List<TypeEquipement> findAllByOrderByNomAsc();
}
