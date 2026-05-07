package com.example.demo.repository;

import com.example.demo.model.Consultation;
import com.example.demo.model.HistoriqueConsultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueConsultationRepository
        extends JpaRepository<HistoriqueConsultation, Integer> {

    List<HistoriqueConsultation> findByConsultationOrderByDateModificationDesc(Consultation consultation);
}

