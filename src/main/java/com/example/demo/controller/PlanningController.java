package com.example.demo.controller;

import com.example.demo.model.Planning;
import com.example.demo.model.Activite;
import com.example.demo.model.Employe;
import com.example.demo.service.PlanningService;
import com.example.demo.service.ActiviteService;
import com.example.demo.service.EmployeService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plannings")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class PlanningController {

    private final PlanningService planningService;
    private final ActiviteService activiteService;
    private final EmployeService employeService;

    public PlanningController(PlanningService planningService,
                              ActiviteService activiteService,
                              EmployeService employeService) {
        this.planningService = planningService;
        this.activiteService = activiteService;
        this.employeService = employeService;
    }

    /* LISTE */
    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        int size = 4;
        Pageable pageable = PageRequest.of(page, size);

        org.springframework.data.domain.Page<Planning> pageResult;

        if (q != null && !q.isBlank()) {
            pageResult = planningService.search(q, pageable);
        } else {
            pageResult = planningService.findAll(pageable);
        }

        model.addAttribute("plannings", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "planning");
        return "plannings/list";
    }

    /* CALENDRIER */
    @GetMapping("/calendar")
    public String calendarView(Model model) {
        model.addAttribute("planningsJson", planningService.getPlanningsAsJson());
        model.addAttribute("activePage", "planning");
        return "plannings/calendar";
    }

    /* CRÉATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(@RequestParam(required = false) String date, Model model) {

        Planning planning = new Planning();

        List<Activite> activites;
        if (date != null) {
            LocalDate localDate = LocalDate.parse(date);
            planning.setDate(localDate);
            activites = planningService.findActivitesByDate(localDate);
        } else {
            activites = activiteService.findAll();
        }

        model.addAttribute("planning", planning);
        model.addAttribute("activites", activites);
        model.addAttribute("employes", employeService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/plannings");
        model.addAttribute("activePage", "planning");

        return "plannings/form";
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Planning planning,
                        BindingResult bindingResult,
                        @RequestParam(required = false) List<Integer> activiteIds,
                        @RequestParam Integer employeId,
                        Model model) {

        Employe responsable = employeService.findById(employeId).orElseThrow();
        planning.setResponsable(responsable);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/plannings");
            // ✅ Remplacer findActivitesSansPlanning() par findActivitesByDate()
            List<Activite> activites = planning.getDate() != null
                ? planningService.findActivitesByDate(planning.getDate())
                : activiteService.findAll();
            model.addAttribute("activites", activites);
            model.addAttribute("employes", employeService.findAll());
            model.addAttribute("activePage", "planning");
            return "plannings/form";
        }

        if (activiteIds != null) {
            for (Integer activiteId : activiteIds) {
                Activite activite = activiteService.findById(activiteId).orElseThrow();
                planning.ajouterActivite(activite);
            }
        }

        try {
            planningService.save(planning);
        } catch (IllegalArgumentException e) {
            model.addAttribute("chevauchementError", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/plannings");
            // ✅ Idem ici
            List<Activite> activites = planning.getDate() != null
                ? planningService.findActivitesByDate(planning.getDate())
                : activiteService.findAll();
            model.addAttribute("activites", activites);
            model.addAttribute("employes", employeService.findAll());
            model.addAttribute("activePage", "planning");
            return "plannings/form";
        }

        return "redirect:/plannings";
    }

    /* DÉTAIL */
    @GetMapping("/{id:\\d+}")
    public String detail(@PathVariable Integer id, Model model) {
        Planning planning = planningService.findById(id).orElseThrow();
        model.addAttribute("planning", planning);
        model.addAttribute("activePage", "planning");
        return "plannings/detail";
    }

    /* MODIFICATION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id, Model model) {

        Planning planning = planningService.findById(id).orElseThrow();

        List<Activite> activites;
        if (planning.getDate() != null) {
            activites = planningService.findActivitesByDate(planning.getDate());
            // S'assurer que les activités déjà liées apparaissent même si date a changé
            planning.getActivites().forEach(a -> {
                if (!activites.contains(a)) activites.add(a);
            });
        } else {
            activites = activiteService.findAll();
        }

        model.addAttribute("planning", planning);
        model.addAttribute("activites", activites);
        model.addAttribute("employes", employeService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/plannings/" + id);
        model.addAttribute("activePage", "planning");

        return "plannings/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute Planning planning,
                        BindingResult bindingResult,
                        @RequestParam(required = false) List<Integer> activiteIds,
                        @RequestParam Integer employeId,
                        Model model) {

        // ← déplacé AVANT le check des erreurs
        Employe responsable = employeService.findById(employeId).orElseThrow();
        planning.setResponsable(responsable);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/plannings/" + id);
            model.addAttribute("activites", activiteService.findAll());
            model.addAttribute("employes", employeService.findAll());
            model.addAttribute("activePage", "planning");
            return "plannings/form";
        }

        planning.getActivites().clear();
        if (activiteIds != null) {
            for (Integer activiteId : activiteIds) {
                Activite activite = activiteService.findById(activiteId).orElseThrow();
                planning.ajouterActivite(activite);
            }
        }

        planningService.update(id, planning);
        return "redirect:/plannings/" + id;
    }

    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {
        planningService.delete(id);
        return "redirect:/plannings";
    }
}