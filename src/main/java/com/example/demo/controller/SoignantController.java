package com.example.demo.controller;

import com.example.demo.model.Soignant;
import com.example.demo.service.SoignantService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/soignants")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
public class SoignantController {

    private final SoignantService soignantService;

    public SoignantController(SoignantService soignantService) {
        this.soignantService = soignantService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("soignants", soignantService.findAll());
        return "soignants/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("soignant", new Soignant());
        return "soignants/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Soignant soignant,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "soignants/form";
        }

        if (soignantService.existsByMatricule(soignant.getMatricule())) {
            bindingResult.rejectValue("matricule", "error.matricule", "Matricule déjà utilisé");
            return "soignants/form";
        }

        soignantService.save(soignant);
        return "redirect:/soignants";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Soignant soignant = soignantService.findById(id).orElseThrow();
        model.addAttribute("soignant", soignant);
        model.addAttribute("consultations", soignant.getConsultations());
        return "soignants/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Soignant soignant = soignantService.findById(id).orElseThrow();
        model.addAttribute("soignant", soignant);
        return "soignants/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Soignant soignant,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "soignants/form";
        }

        soignantService.update(id, soignant);
        return "redirect:/soignants/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        if (soignantService.hasConsultations(id)) {
            return "redirect:/soignants/" + id + "?error=consultations";
        }

        if (soignantService.hasActivites(id)) {
            return "redirect:/soignants/" + id + "?error=activites";
        }

        soignantService.delete(id);
        return "redirect:/soignants";
    }
}
