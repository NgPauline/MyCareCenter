package com.example.demo.repository;

import com.example.demo.model.DossierMedical;
import com.example.demo.model.Resident;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Integer> {

    Optional<DossierMedical> findByResident(Resident resident);

}
