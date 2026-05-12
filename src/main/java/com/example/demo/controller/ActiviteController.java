package com.example.demo.controller;

import com.example.demo.model.Activite;
import com.example.demo.model.Resident;
import com.example.demo.model.Soignant;
import com.example.demo.model.Employe;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.ResidentService;
import com.example.demo.service.SoignantService;
import com.example.demo.service.EmployeService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/activites")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class ActiviteController {

    private final ActiviteService activiteService;
    private final ResidentService residentService;
    private final SoignantService soignantService;
    private final EmployeService employeService;

    public ActiviteController(ActiviteService activiteService,
                              ResidentService residentService,
                              SoignantService soignantService,
                              EmployeService employeService) {
        this.activiteService = activiteService;
        this.residentService = residentService;
        this.soignantService = soignantService;
        this.employeService = employeService;
    }

    /* LISTE */
    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                    @RequestParam(name = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        // ✅ Contexte résident
        if (resident != null) {
            var activites = (q != null && !q.isBlank())
                    ? activiteService.searchByParticipant(resident, q)  // ✅ recherche filtrée
                    : activiteService.findByParticipant(resident);       // liste normale

            model.addAttribute("activites", activites);
            model.addAttribute("resident", resident); // ✅ pour le formulaire de recherche
            model.addAttribute("activePage", "residents");
            return "activites/list";
        }

        // Liste globale (inchangée)
        int size = 4;
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = (q != null && !q.isBlank())
                ? activiteService.search(q, pageable)
                : activiteService.findAll(pageable);

        model.addAttribute("activites", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "activites");
        return "activites/list";
    }

    /* FORMULAIRE CRÉATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
    public String createForm(Model model, Authentication auth) {

        Activite activite = new Activite();

        employeService.findByMatricule(auth.getName()).ifPresent(employe -> {
            if ("EDUCATEUR".equals(employe.getRoleApp())) {
                activite.setResponsable(employe);
            }
        });

        model.addAttribute("activite", activite);
        model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/activites");
        model.addAttribute("activePage", "activites");

        // ✅ Passer le rôle au modèle
        boolean isEducateur = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EDUCATEUR"));
        model.addAttribute("isEducateur", isEducateur);

        return "activites/form";
    }

    /* CRÉATION */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
    public String create(@Valid @ModelAttribute Activite activite,
                        BindingResult bindingResult,
                        @RequestParam Integer responsableId,
                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/activites");
            model.addAttribute("activePage", "activites");
            return "activites/form";
        }

        try {
            Employe responsable = employeService.findById(responsableId).orElseThrow();
            activite.setResponsable(responsable);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employe createur = employeService.findByMatricule(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));

            activiteService.save(activite, createur);

        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("heureDebut", "error.activite", e.getMessage());
            model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/activites");
            model.addAttribute("activePage", "activites");
            return "activites/form";
        }

        return "redirect:/activites";
    }

    /* DÉTAIL */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Activite activite = activiteService.findById(id).orElseThrow();
        model.addAttribute("activite", activite);
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("activePage", "activites");
        return "activites/detail";
    }

    /* FORMULAIRE MODIFICATION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
    public String editForm(@PathVariable Integer id, Model model) {
        Activite activite = activiteService.findById(id).orElseThrow();
        model.addAttribute("activite", activite);
        model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/activites/" + id);
        model.addAttribute("activePage", "activites");
        return "activites/form";
    }

    /* MODIFICATION */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute Activite activite,
                        BindingResult bindingResult,
                        @RequestParam(required = false) Integer responsableId,
                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/activites/" + id);
            model.addAttribute("activePage", "activites");
            return "activites/form";
        }

        if (activiteService.isActivitePassee(id)) {
            return "redirect:/activites/" + id + "?error=passee";
        }

        try {
            if (responsableId != null) {
                Employe responsable = employeService.findById(responsableId).orElseThrow();
                activite.setResponsable(responsable);
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employe createur = employeService.findByMatricule(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));

            activiteService.update(id, activite, createur);

        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("heureDebut", "error.activite", e.getMessage());
            model.addAttribute("employes", employeService.findByRole("EDUCATEUR"));
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/activites/" + id);
            model.addAttribute("activePage", "activites");
            return "activites/form";
        }

        return "redirect:/activites/" + id;
    }

    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {
        if (activiteService.isActivitePassee(id)) {
            return "redirect:/activites/" + id + "?error=passee";
        }
        activiteService.delete(id);
        return "redirect:/activites";
    }

    /* INSCRIPTION */
@PostMapping("/{id}/inscrire")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
public String inscrire(@PathVariable Integer id,
                       @RequestParam Integer residentId) {
    Activite activite = activiteService.findById(id).orElseThrow();
    Resident resident = residentService.findById(residentId).orElseThrow();

    try {
        activiteService.inscrireResident(activite, resident);
    } catch (IllegalArgumentException e) {
        if (e.getMessage().contains("complète")) {
            return "redirect:/activites/" + id + "?error=complet";
        }
        return "redirect:/activites/" + id + "?error=inscription";
    }

    return "redirect:/activites/" + id; 
}

    /* DÉSINSCRIPTION */
    @PostMapping("/{id}/desinscrire")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','EDUCATEUR')")
    public String desinscrire(@PathVariable Integer id,
                              @RequestParam Integer residentId) {
        Activite activite = activiteService.findById(id).orElseThrow();
        Resident resident = residentService.findById(residentId).orElseThrow();
        activiteService.desinscrireResident(activite, resident);
        return "redirect:/activites/" + id;
    }
}
