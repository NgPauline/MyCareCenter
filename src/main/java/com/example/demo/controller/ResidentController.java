package com.example.demo.controller;

import com.example.demo.model.Resident;
import com.example.demo.model.Chambre;
import com.example.demo.service.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/residents")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class ResidentController {

    private final ResidentService residentService;
    private final ChambreService chambreService;
    private final FamilleService familleService;
    private final FactureService factureService;
    private final ActiviteService activiteService;
    private final ConsultationService consultationService;
    private final TraitementService traitementService;

    public ResidentController(ResidentService residentService,
                              ChambreService chambreService,
                              FamilleService familleService,
                              FactureService factureService,
                              ActiviteService activiteService,
                              ConsultationService consultationService,
                              TraitementService traitementService) {
        this.residentService = residentService;
        this.chambreService = chambreService;
        this.familleService = familleService;
        this.factureService = factureService;
        this.activiteService = activiteService;
        this.consultationService = consultationService;
        this.traitementService = traitementService;
    }

    /* LISTE + RECHERCHE */
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        int size = 4;
        org.springframework.data.domain.Pageable pageable =
            org.springframework.data.domain.PageRequest.of(page, size);

        org.springframework.data.domain.Page<Resident> pageResult;

        if (q != null && !q.trim().isEmpty()) {
            pageResult = residentService.search(q.trim(), pageable);
        } else {
            pageResult = residentService.findAll(pageable);
        }

        model.addAttribute("residents", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "residents");
        return "residents/list";
    }

    /* FORMULAIRE CREATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(Model model) {

        model.addAttribute("resident", new Resident());
        model.addAttribute("chambres", chambreService.findChambresLibres());
        model.addAttribute("familles", familleService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/residents");
        model.addAttribute("activePage", "residents"); // 👈

        return "residents/form";
    }

    /* CREATION */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Resident resident,
                         BindingResult bindingResult,
                         @RequestParam(required = false) Integer chambreId,
                         @RequestParam(required = false) Integer familleId,
                         Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("chambres", chambreService.findChambresLibres());
            model.addAttribute("familles", familleService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/residents");
            model.addAttribute("activePage", "residents"); // 👈

            return "residents/form";
        }

        if (chambreId != null) {
            Chambre chambre = chambreService.findById(chambreId).orElseThrow();
            resident.setChambre(chambre);
            chambre.setOccupant(resident);
        }

        if (familleId != null) {
            resident.ajouterFamille(familleService.findById(familleId).orElseThrow());
        }

        residentService.save(resident);
        return "redirect:/residents";
    }

    /* DETAIL */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Resident resident = residentService.findById(id).orElseThrow();

        java.util.List<com.example.demo.model.Consultation> consultations =
            new java.util.ArrayList<>(consultationService.findByResident(resident));
        consultations.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        model.addAttribute("resident", resident);
        model.addAttribute("factures", factureService.findByResident(resident));
        model.addAttribute("activites", activiteService.findByResident(resident));
        model.addAttribute("familles", resident.getFamilles());
        model.addAttribute("consultations", consultations);
        model.addAttribute("traitements", traitementService.findByResident(resident));
        model.addAttribute("activePage", "residents");
        return "residents/detail";
    }

    /* FORMULAIRE EDITION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id, Model model) {

        Resident resident = residentService.findById(id).orElseThrow();

        model.addAttribute("resident", resident);
        model.addAttribute("chambres", chambreService.findChambresLibresOuActuelle(resident));
        model.addAttribute("familles", familleService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/residents/" + id);
        model.addAttribute("activePage", "residents"); // 👈

        return "residents/form";
    }

    /* EDITION */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Resident resident,
                         BindingResult bindingResult,
                         @RequestParam(required = false) Integer chambreId,
                         @RequestParam(required = false) Integer familleId,
                         Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("chambres", chambreService.findChambresLibresOuActuelle(resident));
            model.addAttribute("familles", familleService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/residents/" + id);
            model.addAttribute("activePage", "residents"); // 👈

            return "residents/form";
        }

        if (chambreId != null) {
            Chambre chambre = chambreService.findById(chambreId).orElseThrow();
            resident.setChambre(chambre);
            chambre.setOccupant(resident);
        } else {
            resident.setChambre(null);
        }

        if (familleId != null) {
            resident.ajouterFamille(familleService.findById(familleId).orElseThrow());
        }

        residentService.update(id, resident);
        return "redirect:/residents/" + id;
    }

    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {

        Resident resident = residentService.findById(id).orElseThrow();

        if (resident.getChambre() != null)
            return "redirect:/residents/" + id + "?error=chambre";

        if (factureService.hasFacturesImpayees(id))
            return "redirect:/residents/" + id + "?error=impaye";

        if (!consultationService.findByResident(resident).isEmpty())
            return "redirect:/residents/" + id + "?error=consultations";

        if (!traitementService.findByResident(resident).isEmpty())
            return "redirect:/residents/" + id + "?error=traitements";

        if (!activiteService.findByResident(resident).isEmpty())
            return "redirect:/residents/" + id + "?error=activites";

        residentService.delete(id);
        return "redirect:/residents";
    }
}