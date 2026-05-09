package com.example.demo.controller;

import com.example.demo.model.Consultation;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import com.example.demo.service.ConsultationService;
import com.example.demo.service.ResidentService;
import com.example.demo.service.SoignantService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/consultations")
@PreAuthorize("hasAnyRole('DIRECTEUR','SOIGNANT')")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final ResidentService residentService;
    private final SoignantService soignantService;

    public ConsultationController(ConsultationService consultationService,
                                  ResidentService residentService,
                                  SoignantService soignantService) {
        this.consultationService = consultationService;
        this.residentService = residentService;
        this.soignantService = soignantService;
    }

    /* ---------------- LISTE ---------------- */
    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                    @RequestParam(required = false) Integer soignant,
                    @RequestParam(name = "q", required = false) String q,
                    Model model) {

        if (resident != null) {
            Resident r = residentService.findById(resident).orElseThrow();
            var consultations = (q != null && !q.isBlank())
                    ? consultationService.searchByResident(r, q)
                    : consultationService.findByResident(r);

            model.addAttribute("consultations", consultations);
            model.addAttribute("residentId", resident);
            model.addAttribute("q", q);
            model.addAttribute("activePage", "residents");
            return "consultations/list";
        }

        if (soignant != null) {
            Soignant s = soignantService.findById(soignant).orElseThrow();
            model.addAttribute("consultations", consultationService.findBySoignant(s));
            return "consultations/list";
        }

        model.addAttribute("consultations", consultationService.findAll());
        return "consultations/list";
    }

    /* ---------------- FORMULAIRE CREATION ---------------- */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer residentId, Model model) {

        model.addAttribute("consultation", new Consultation());
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("soignants", soignantService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/consultations");
        model.addAttribute("residentId", residentId);

        // Résident pré-rempli
        if (residentId != null) {
            residentService.findById(residentId)
                .ifPresent(r -> model.addAttribute("residentPreRempli", r)); 
        }

            // Passer le soignant connecté si rôle SOIGNANT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            soignantService.findByMatricule(auth.getName())
                .ifPresent(s -> model.addAttribute("soignantConnecte", s));
        
        return "consultations/form";
    }


    /* ---------------- CREATION ---------------- */
    @PostMapping
    public String create(@Valid @ModelAttribute Consultation consultation,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         @RequestParam Integer soignantId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("soignants", soignantService.findAll());
            return "consultations/form";
        }

        Resident resident = residentService.findById(residentId).orElseThrow();
        Soignant soignant = soignantService.findById(soignantId).orElseThrow();

        /* Règle métier : consultation pas dans le futur */
        if (consultation.getDate().isAfter(LocalDateTime.now())) {
            bindingResult.rejectValue("date", "error.date", "La consultation ne peut pas être dans le futur");
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("soignants", soignantService.findAll());
            return "consultations/form";
        }

        /* Règle métier : dossier médical obligatoire */
        if (resident.getDossierMedical() == null) {
            bindingResult.rejectValue("resident", "error.resident", "Le résident n'a pas de dossier médical");
            return "consultations/form";
        }

        consultation.setResident(resident);
        consultation.setSoignant(soignant);
        consultation.setDossierMedical(resident.getDossierMedical());

        consultationService.save(consultation);
        return "redirect:/consultations";
    }

    /* ---------------- DETAIL ---------------- */
    @GetMapping("/{idConsultation}")
    public String detail(@PathVariable Integer idConsultation, Model model) {

        Consultation consultation = consultationService.findById(idConsultation).orElseThrow();

        model.addAttribute("consultation", consultation);
        model.addAttribute("historique", consultationService.findHistorique(consultation));

        return "consultations/detail";
    }

    /* ---------------- FORMULAIRE EDITION ---------------- */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {

        Consultation consultation = consultationService.findById(id).orElseThrow();

        if (consultation.getDate().isBefore(LocalDateTime.now())) {
            return "redirect:/consultations/" + id + "?error=nonModifiable";
        }

        model.addAttribute("consultation", consultation);
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("soignants", soignantService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/consultations/" + id);

        return "consultations/form";
    }


    /* ---------------- EDITION ---------------- */
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Consultation consultation,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         @RequestParam Integer soignantId,
                         Model model) {

        Consultation original = consultationService.findById(id).orElseThrow();

        /* Règle métier : consultation passée non modifiable */
        if (original.getDate().isBefore(LocalDateTime.now())) {
            return "redirect:/consultations/" + id + "?error=nonModifiable";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("soignants", soignantService.findAll());
            return "consultations/form";
        }

        Resident resident = residentService.findById(residentId).orElseThrow();
        Soignant soignant = soignantService.findById(soignantId).orElseThrow();

        /* Règle métier : on ne change PAS le résident */
        consultation.setResident(original.getResident());
        consultation.setDossierMedical(original.getDossierMedical());

        /* Règle métier : on peut changer le soignant (optionnel) */
        consultation.setSoignant(soignant);

        consultationService.update(id, consultation);
        return "redirect:/consultations/" + id;
    }

    /* ---------------- SUPPRESSION ---------------- */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        Consultation consultation = consultationService.findById(id).orElseThrow();

        /* Règle métier : consultation passée non supprimable */
        if (consultation.getDate().isBefore(LocalDateTime.now())) {
            return "redirect:/consultations/" + id + "?error=nonSupprimable";
        }

        consultationService.delete(id);
        return "redirect:/consultations";
    }
}
