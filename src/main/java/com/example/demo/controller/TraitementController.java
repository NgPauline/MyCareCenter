package com.example.demo.controller;

import com.example.demo.model.Traitement;
import com.example.demo.model.Resident;
import com.example.demo.service.TraitementService;
import com.example.demo.service.ResidentService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/traitements")
@PreAuthorize("hasAnyRole('DIRECTEUR','SOIGNANT')")
public class TraitementController {

    private final TraitementService traitementService;
    private final ResidentService residentService;

    public TraitementController(TraitementService traitementService,
                                ResidentService residentService) {
        this.traitementService = traitementService;
        this.residentService = residentService;
    }

    /* ---------------- LISTE ---------------- */
    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                    @RequestParam(name = "q", required = false) String q,
                    Model model) {

        if (resident != null) {
            Resident r = residentService.findById(resident).orElseThrow();
            var traitements = (q != null && !q.isBlank())
                    ? traitementService.searchByResident(r, q)
                    : traitementService.findByResident(r);

            model.addAttribute("traitements", traitements);
            model.addAttribute("residentId", resident);
            model.addAttribute("q", q);
            return "traitements/list";
        }

        model.addAttribute("traitements", traitementService.findAll());
        return "traitements/list";
    }

    /* ---------------- FORMULAIRE CREATION ---------------- */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer residentId, Model model) {
        model.addAttribute("traitement", new Traitement());
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/traitements");
        model.addAttribute("residentId", residentId);

        if (residentId != null) {
        residentService.findById(residentId)
            .ifPresent(r -> model.addAttribute("residentPreRempli", r)); 
        }

        return "traitements/form";
    }

    /* ---------------- CREATION ---------------- */
    @PostMapping
    public String create(@Valid @ModelAttribute Traitement traitement,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/traitements");
            return "traitements/form";
        }

        Resident resident = residentService.findById(residentId).orElseThrow();
        traitement.setDossierMedical(resident.getDossierMedical());

        try {
            traitementService.save(traitement);
        } catch (IllegalArgumentException e) {
            model.addAttribute("dateError", e.getMessage());
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/traitements");
            return "traitements/form";
        }

        return "redirect:/traitements";
    }

    /* ---------------- DETAIL ---------------- */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Traitement traitement = traitementService.findById(id).orElseThrow();
        model.addAttribute("traitement", traitement);
        return "traitements/detail";
    }

    /* ---------------- FORMULAIRE EDITION ---------------- */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Traitement traitement = traitementService.findById(id).orElseThrow();
        model.addAttribute("traitement", traitement);
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/traitements/" + id);
        return "traitements/form";
    }

    /* ---------------- EDITION ---------------- */
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Traitement traitement,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/traitements/" + id);
            return "traitements/form";
        }

        Resident resident = residentService.findById(residentId).orElseThrow();
        traitement.setDossierMedical(resident.getDossierMedical());

        try {
            traitementService.update(id, traitement);
        } catch (IllegalArgumentException e) {
            model.addAttribute("dateError", e.getMessage());
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/traitements/" + id);
            return "traitements/form";
        }

        return "redirect:/traitements/" + id;
    }

    /* ---------------- SUPPRESSION ---------------- */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        traitementService.delete(id);
        return "redirect:/traitements";
    }
}
