package com.example.demo.controller;

import com.example.demo.model.Administratif;
import com.example.demo.service.AdministratifService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/administratifs")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
public class AdministratifController {

    private final AdministratifService administratifService;

    public AdministratifController(AdministratifService administratifService) {
        this.administratifService = administratifService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("administratifs", administratifService.findAll());
        return "administratifs/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String createForm(Model model) {
        model.addAttribute("administratif", new Administratif());
        return "administratifs/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String create(@Valid @ModelAttribute Administratif administratif,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "administratifs/form";
        }

        if (administratifService.existsByMatricule(administratif.getMatricule())) {
            bindingResult.rejectValue("matricule", "error.matricule", "Matricule déjà utilisé");
            return "administratifs/form";
        }

        administratifService.save(administratif);
        return "redirect:/administratifs";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Administratif administratif = administratifService.findById(id).orElseThrow();
        model.addAttribute("administratif", administratif);
        return "administratifs/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String editForm(@PathVariable Integer id, Model model) {
        Administratif administratif = administratifService.findById(id).orElseThrow();
        model.addAttribute("administratif", administratif);
        return "administratifs/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Administratif administratif,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "administratifs/form";
        }

        Administratif original = administratifService.findById(id).orElseThrow();
        administratif.setMatricule(original.getMatricule());

        administratifService.update(id, administratif);
        return "redirect:/administratifs/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String delete(@PathVariable Integer id) {

        if (administratifService.hasPlanningResponsable(id)) {
            return "redirect:/administratifs/" + id + "?error=planning";
        }

        administratifService.delete(id);
        return "redirect:/administratifs";
    }
}
