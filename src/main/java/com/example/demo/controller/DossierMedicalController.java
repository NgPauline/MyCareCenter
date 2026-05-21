package com.example.demo.controller;

import com.example.demo.model.DossierMedical;
import com.example.demo.model.Resident;
import com.example.demo.service.DossierMedicalService;
import com.example.demo.service.ResidentService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dossiers")
@PreAuthorize("hasAnyRole('DIRECTEUR','SOIGNANT')")
public class DossierMedicalController {

    private final DossierMedicalService dossierService;
    private final ResidentService residentService;

    public DossierMedicalController(DossierMedicalService dossierService,
                                    ResidentService residentService) {
        this.dossierService = dossierService;
        this.residentService = residentService;
    }

    /* -------------------- DETAIL -------------------- */
    @GetMapping("/{residentId}")
    public String detail(@PathVariable Integer residentId, Model model) {
        Resident resident = residentService.findById(residentId)
                .orElseThrow(() -> new IllegalArgumentException("Résident introuvable"));

        if (resident.getDossierMedical() == null) {
            DossierMedical nouveau = dossierService.createForResident(resident);
            resident.setDossierMedical(nouveau);
            residentService.save(resident);
        }

        model.addAttribute("resident", resident);
        model.addAttribute("dossier", resident.getDossierMedical());
        model.addAttribute("activePage", "residents");
        return "dossiers/detail";
    }

    /* -------------------- FORMULAIRE EDITION -------------------- */
    @GetMapping("/{residentId}/edit")
    public String editForm(@PathVariable Integer residentId, Model model) {
        Resident resident = residentService.findById(residentId)
                .orElseThrow(() -> new IllegalArgumentException("Résident introuvable"));

        if (resident.getDossierMedical() == null) {
            DossierMedical nouveau = dossierService.createForResident(resident);
            resident.setDossierMedical(nouveau);
            residentService.save(resident);
        }

        model.addAttribute("resident", resident);
        model.addAttribute("dossier", resident.getDossierMedical());
        model.addAttribute("activePage", "residents");
        return "dossiers/form";
    }

    /* -------------------- MISE À JOUR -------------------- */
    @PostMapping("/{residentId}")
    public String update(@PathVariable Integer residentId,
                         @Valid @ModelAttribute("dossier") DossierMedical dossierForm,
                         BindingResult bindingResult,
                         Model model) {

        Resident resident = residentService.findById(residentId)
                .orElseThrow(() -> new IllegalArgumentException("Résident introuvable"));

        DossierMedical dossier = resident.getDossierMedical();
        if (dossier == null) {
            dossier = dossierService.createForResident(resident);
            resident.setDossierMedical(dossier);
            residentService.save(resident);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("resident", resident);
            model.addAttribute("dossier", dossierForm);
            model.addAttribute("activePage", "residents");
            return "dossiers/form";
        }

        dossierForm.setDateCreation(dossier.getDateCreation());
        dossierService.updateForResident(residentId, dossierForm);
        return "redirect:/dossiers/" + residentId;
    }
}
