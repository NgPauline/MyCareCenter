package com.example.demo.controller;

import com.example.demo.model.Employe;
import com.example.demo.service.EmployeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employes")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
public class EmployeController {

    private final EmployeService employeService;

    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        int size = 4;
        Pageable pageable = PageRequest.of(page, size);

        org.springframework.data.domain.Page<Employe> pageResult;

        if (q != null && !q.isBlank()) {
            pageResult = employeService.search(q, pageable);
        } else {
            pageResult = employeService.findAll(pageable);
        }

        model.addAttribute("employes", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "employes");
        return "employes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("employe", new Employe());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/employes");
        model.addAttribute("activePage", "employes"); // 👈

        return "employes/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Employe employe,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/employes");
            model.addAttribute("activePage", "employes"); // 👈
            return "employes/form";
        }

        employeService.save(employe);
        return "redirect:/employes";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Employe employe = employeService.findById(id).orElseThrow();
        model.addAttribute("employe", employe);
        model.addAttribute("activePage", "employes"); // 👈
        return "employes/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {

        Employe employe = employeService.findById(id).orElseThrow();
        model.addAttribute("employe", employe);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/employes/" + id);
        model.addAttribute("activePage", "employes"); // 👈

        return "employes/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Employe employe,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/employes/" + id);
            model.addAttribute("activePage", "employes"); // 👈
            return "employes/form";
        }

        Employe original = employeService.findById(id).orElseThrow();
        employe.setMatricule(original.getMatricule());

        employeService.update(id, employe);
        return "redirect:/employes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        if (employeService.hasPlanningResponsable(id)) {
            return "redirect:/employes/" + id + "?error=planning";
        }

        employeService.delete(id);
        return "redirect:/employes";
    }
}