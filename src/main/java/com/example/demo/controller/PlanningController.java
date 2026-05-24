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

        org.springframework.data.domain.Page<Planning> pageResult = (q != null && !q.isBlank())
                ? planningService.search(q, pageable)
                : planningService.findAll(pageable);

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

    /* FORMULAIRE CRÉATION */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(Model model) {
        model.addAttribute("planning", new Planning());
        model.addAttribute("employes", employeService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/plannings");
        model.addAttribute("activePage", "planning");
        return "plannings/form";
    }

    /* CRÉATION */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Planning planning,
                        BindingResult bindingResult,
                        @RequestParam Integer employeId,
                        Model model) {

        Employe responsable = employeService.findById(employeId).orElseThrow();
        planning.setResponsable(responsable);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/plannings");
            model.addAttribute("employes", employeService.findAll());
            model.addAttribute("activePage", "planning");
            return "plannings/form";
        }

        try {
            planningService.save(planning);
        } catch (IllegalArgumentException e) {
            model.addAttribute("chevauchementError", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/plannings");
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

        List<Activite> activitesDuJour = activiteService.findAll().stream()
            .filter(a -> planning.getDate().equals(a.getDate())
                      && planning.getResponsable().equals(a.getResponsable()))
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("planning", planning);
        model.addAttribute("activitesDuJour", activitesDuJour);
        model.addAttribute("activePage", "planning");
        return "plannings/detail";
    }

    /* FORMULAIRE MODIFICATION */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id, Model model) {
        Planning planning = planningService.findById(id).orElseThrow();
        model.addAttribute("planning", planning);
        model.addAttribute("employes", employeService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/plannings/" + id);
        model.addAttribute("activePage", "planning");
        return "plannings/form";
    }

    /* MODIFICATION */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute Planning planning,
                        BindingResult bindingResult,
                        @RequestParam Integer employeId,
                        Model model) {

        Employe responsable = employeService.findById(employeId).orElseThrow();
        planning.setResponsable(responsable);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/plannings/" + id);
            model.addAttribute("employes", employeService.findAll());
            model.addAttribute("activePage", "planning");
            return "plannings/form";
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
