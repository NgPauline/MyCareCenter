package com.example.demo.repository;

import com.example.demo.model.Administratif;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratifRepository extends JpaRepository<Administratif, Integer> {

    boolean existsByMatricule(String matricule);
    
    Administratif findByMatricule(String matricule); 
}

