package com.example.demo.controller;

import com.example.demo.model.Famille;
import com.example.demo.model.Resident;
import com.example.demo.service.FamilleService;
import com.example.demo.service.ResidentService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/familles")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class FamilleController {

    private final FamilleService familleService;
    private final ResidentService residentService;

    public FamilleController(FamilleService familleService,
                             ResidentService residentService) {
        this.familleService = familleService;
        this.residentService = residentService;
    }

    /* LISTE */
    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                       Model model) {

        if (resident != null) {
            model.addAttribute("familles", familleService.findByResidentId(resident));
            model.addAttribute("residentId", resident);
            model.addAttribute("activePage", "residents");
            return "familles/list";
        }

        model.addAttribute("familles", familleService.findAll());
        model.addAttribute("activePage", "familles");
        return "familles/list";
    }

    /* FORMULAIRE CREATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(@RequestParam(required = false) Integer residentId,
                             Model model) {

        model.addAttribute("famille", new Famille());
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("residentId", residentId);
        model.addAttribute("submitUrl", "/familles");
        model.addAttribute("activePage", "familles");

         if (residentId != null) {
            residentService.findById(residentId)
                .ifPresent(r -> model.addAttribute("residentPreRempli", r));
        }
        
        return "familles/form";
    }

    /* CREATION */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Famille famille,
                        BindingResult bindingResult,
                        @RequestParam(required = false) Integer residentId,
                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/familles");
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("activePage", "familles");
            return "familles/form";
        }

        //  Sauvegarde de la famille
        Famille savedFamille = familleService.save(famille);

        //  Association au résident si fourni
        if (residentId != null) {
            Resident resident = residentService.findById(residentId).orElseThrow();
            resident.ajouterFamille(savedFamille);
            residentService.save(resident);

            //  Redirection vers le résident
            return "redirect:/residents/" + residentId;
        }

        //  Sinon, retour à la liste des familles
        return "redirect:/familles";
    }


    /* DETAIL */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @RequestParam(required = false) Integer resident,
                         Model model) {

        Famille famille = familleService.findById(id).orElseThrow();

        model.addAttribute("famille", famille);
        model.addAttribute("residents", famille.getResidents());
        model.addAttribute("residentId", resident);
        model.addAttribute("activePage", "familles");

        return "familles/detail";
    }

    /* FORMULAIRE EDITION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id,
                        @RequestParam(required = false) Integer residentId, 
                        Model model) {

        Famille famille = familleService.findById(id).orElseThrow();

        model.addAttribute("famille", famille);
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/familles/" + id);
        model.addAttribute("activePage", "familles");
        model.addAttribute("residentId", residentId); 

        return "familles/form";
    }

    /* EDITION */
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute Famille famille,
                        BindingResult bindingResult,
                        @RequestParam(required = false) Integer residentId,
                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/familles/" + id);
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("activePage", "familles");
            return "familles/form";
        }

        familleService.update(id, famille);

        if (residentId != null) {
            return "redirect:/familles?resident=" + residentId;
        }

        return "redirect:/familles";
    }


    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {
        familleService.delete(id);
        return "redirect:/familles";
    }
}
