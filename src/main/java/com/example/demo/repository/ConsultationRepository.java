package com.example.demo.repository;

import com.example.demo.model.Consultation;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

    List<Consultation> findByResident(Resident resident);

    List<Consultation> findBySoignant(Soignant soignant);

    List<Consultation> findByDateBetween(LocalDateTime debut, LocalDateTime fin);

    List<Consultation> findByResidentAndDateBetween(Resident resident, LocalDateTime debut, LocalDateTime fin);
}
