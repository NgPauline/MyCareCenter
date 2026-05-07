package com.example.demo.repository;

import com.example.demo.model.Soignant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoignantRepository extends JpaRepository<Soignant, Integer> {

    boolean existsByMatricule(String matricule);

    Soignant findByMatricule(String matricule);
}
