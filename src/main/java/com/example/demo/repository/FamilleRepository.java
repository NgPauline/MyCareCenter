package com.example.demo.repository;

import com.example.demo.model.Famille;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilleRepository extends JpaRepository<Famille, Integer> {
    List<Famille> findByResidents_IdPersonne(Integer idPersonne);


}
