package com.example.demo.service;

import com.example.demo.model.Paiement;
import com.example.demo.model.Facture;
import com.example.demo.repository.PaiementRepository;
import com.example.demo.repository.FactureRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;

    public PaiementService(PaiementRepository paiementRepository,
                           FactureRepository factureRepository) {
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
    }

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public Optional<Paiement> findById(Integer id) {
        return paiementRepository.findById(id);
    }

    public Paiement save(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public void delete(Integer id) {
        paiementRepository.deleteById(id);
    }

    public List<Paiement> findByResidentId(Integer residentId) {
    return paiementRepository.findByFacture_Resident_IdPersonne(residentId);
}

}
