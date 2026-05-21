package com.example.demo.controller;

import com.example.demo.model.Equipement;
import com.example.demo.model.TypeEquipement;
import com.example.demo.service.ChambreService;
import com.example.demo.service.EquipementService;
import com.example.demo.service.TypeEquipementService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/equipements")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class EquipementController {

    private final EquipementService equipementService;
    private final ChambreService chambreService;
    private final TypeEquipementService typeEquipementService;

    public EquipementController(EquipementService equipementService,
                                ChambreService chambreService,
                                TypeEquipementService typeEquipementService) {
        this.equipementService = equipementService;
        this.chambreService = chambreService;
        this.typeEquipementService = typeEquipementService;
    }

    /* LISTE GÉNÉRALE */
    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer chambre,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        if (chambre != null) {
            model.addAttribute("equipements", equipementService.findByChambre(chambre));
            model.addAttribute("idChambre", chambre);
            model.addAttribute("activePage", "chambres");
            return "equipements/list-chambre";
        }

        int size = 6;
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = (q != null && !q.isEmpty())
                ? equipementService.search(q, pageable)
                : equipementService.findAll(pageable);

        model.addAttribute("equipements", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "types-equipement");
        return "equipements/list";
    }

    /* LISTE PAR CHAMBRE */
    @GetMapping("/chambre/{idChambre}")
    public String listByChambre(@PathVariable Integer idChambre, Model model) {
        model.addAttribute("equipements", equipementService.findByChambre(idChambre));
        model.addAttribute("idChambre", idChambre);
        model.addAttribute("activePage", "chambres");
        return "equipements/list-chambre";
    }

    /* FORMULAIRE CRÉATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(@RequestParam(required = false) Integer typeId,
                            @RequestParam(required = false) Integer idChambre,
                            Model model) {

        Equipement equipement = new Equipement();

        if (idChambre != null) {
            chambreService.findById(idChambre).ifPresent(equipement::setChambre);
        }

        model.addAttribute("equipement", equipement);
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/equipements");
        model.addAttribute("chambres", chambreService.findAll());
        model.addAttribute("types", typeEquipementService.findAll());
        model.addAttribute("idChambre", idChambre);
        model.addAttribute("activePage", idChambre != null ? "chambres" : "types-equipement");

        if (typeId != null) {
            typeEquipementService.findById(typeId)
                .ifPresent(t -> model.addAttribute("typePreRempli", t));
        }

        return "equipements/form";
    }

    /* CRÉATION */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Equipement equipement,
                        BindingResult bindingResult,
                        @RequestParam Integer typeId,
                        @RequestParam(required = false) Integer chambreId,
                        Model model) {

        TypeEquipement type = typeEquipementService.findById(typeId).orElseThrow();
        equipement.setType(type);

        if (chambreId != null) {
            equipement.setChambre(chambreService.findById(chambreId).orElse(null));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            model.addAttribute("typePreRempli", type);
            model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");
            return "equipements/form";
        }

        long restant = type.getQuantiteTotale() - equipementService.countByType(type.getId());
        if (restant <= 0) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            model.addAttribute("typePreRempli", type);
            model.addAttribute("errorStock", "Stock insuffisant pour ce type d'équipement.");
            model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");
            return "equipements/form";
        }

        if (chambreId != null && equipementService.existsByChambreAndType(chambreId, typeId)) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            model.addAttribute("typePreRempli", type);
            model.addAttribute("errorStock",
                "Un équipement de ce type est déjà affecté à cette chambre.");
            model.addAttribute("activePage", "chambres");
            return "equipements/form";
        }

        equipementService.save(equipement);
        return "redirect:/types-equipement/" + typeId;
    }

    /* FORMULAIRE ÉDITION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id,
                           @RequestParam(required = false) Integer idChambre,
                           Model model) {

        Equipement equipement = equipementService.findById(id).orElseThrow();
        model.addAttribute("equipement", equipement);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/equipements/" + id);
        model.addAttribute("chambres", chambreService.findAll());
        model.addAttribute("idChambre", idChambre);
        model.addAttribute("activePage", idChambre != null ? "chambres" : "types-equipement");
        return "equipements/form";
    }

    /* MISE À JOUR */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Equipement equipementForm,
                         BindingResult bindingResult,
                         @RequestParam(required = false) Integer chambreId,
                         @RequestParam(required = false) Integer idChambre,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("activePage", idChambre != null ? "chambres" : "types-equipement");
            return "equipements/form";
        }

        Equipement existing = equipementService.findById(id).orElseThrow();

        existing.setEtat(equipementForm.getEtat());
        existing.setChambre(chambreId != null
                ? chambreService.findById(chambreId).orElse(null)
                : null);

        if (chambreId != null) {
            boolean autreExemplaire = equipementService.existsByChambreAndType(chambreId, existing.getType().getId());
            boolean memeChambre = existing.getChambre() != null
                                && existing.getChambre().getIdChambre().equals(chambreId);
            if (autreExemplaire && !memeChambre) {
                model.addAttribute("chambres", chambreService.findAll());
                model.addAttribute("errorStock",
                    "Un équipement de ce type est déjà affecté à cette chambre.");
                model.addAttribute("activePage", "chambres");
                return "equipements/form";
            }
        }

        equipementService.update(id, existing);
        return "redirect:/types-equipement/" + existing.getType().getId();
    }

    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {
        Equipement equipement = equipementService.findById(id).orElseThrow();
        Integer typeId = equipement.getType().getId();
        equipementService.delete(id);
        return "redirect:/types-equipement/" + typeId;
    }

    /* REDIRECTION */
    @GetMapping("/{id}")
    public String redirectToList() {
        return "redirect:/types-equipement";
    }
}