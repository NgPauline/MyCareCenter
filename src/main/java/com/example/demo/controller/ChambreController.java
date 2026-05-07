package com.example.demo.controller;

import com.example.demo.model.Chambre;
import com.example.demo.model.Resident;
import com.example.demo.service.ChambreService;
import com.example.demo.service.ResidentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chambres")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF','SOIGNANT','EDUCATEUR')")
public class ChambreController {

    private final ChambreService chambreService;
    private final ResidentService residentService;

    public ChambreController(ChambreService chambreService,
                             ResidentService residentService) {
        this.chambreService = chambreService;
        this.residentService = residentService;
    }

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        int size = 4;
        Pageable pageable = PageRequest.of(page, size);

        org.springframework.data.domain.Page<Chambre> pageResult;

        if (q != null && !q.isBlank()) {
            pageResult = chambreService.search(q, pageable);
        } else {
            pageResult = chambreService.findAll(pageable);
        }

        model.addAttribute("chambres", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "chambres");
        return "chambres/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String createForm(Model model) {
        model.addAttribute("chambre", new Chambre());
        model.addAttribute("isEdit", false);           // ← ajouter
        model.addAttribute("submitUrl", "/chambres"); 
        model.addAttribute("activePage", "chambres"); 
        return "chambres/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String create(@Valid @ModelAttribute Chambre chambre,
                         BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);          
            model.addAttribute("submitUrl", "/chambres");
            return "chambres/form";
        }

        if (chambreService.existsByNumero(chambre.getNumero())) {
            bindingResult.rejectValue("numero", "error.numero", "Numéro déjà utilisé");
            model.addAttribute("isEdit", false);          
            model.addAttribute("submitUrl", "/chambres");
            return "chambres/form";
        }

        chambreService.save(chambre);
        return "redirect:/chambres";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Chambre chambre = chambreService.findById(id).orElseThrow();
        model.addAttribute("chambre", chambre);
        model.addAttribute("resident", chambre.getOccupant());
        model.addAttribute("activePage", "chambres"); // 👈
        return "chambres/detail";
    }

    @GetMapping("/{id}/attribuer")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String attribuerForm(@PathVariable Integer id, Model model) {
        Chambre chambre = chambreService.findById(id).orElseThrow();

        if (chambre.getOccupant() != null) {
            return "redirect:/chambres/" + id + "?error=occupee";
        }

        model.addAttribute("chambre", chambre);
        model.addAttribute("residents", residentService.findResidentsSansChambre());
        model.addAttribute("activePage", "chambres"); // 👈
        return "chambres/attribuer";
    }

    @PostMapping("/{id}/attribuer")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String attribuer(@PathVariable Integer id,
                            @RequestParam Integer residentId) {

        Chambre chambre = chambreService.findById(id).orElseThrow();
        Resident resident = residentService.findById(residentId).orElseThrow();

        if (chambre.getOccupant() != null) {
            return "redirect:/chambres/" + id + "?error=occupee";
        }

        chambre.setOccupant(resident);
        resident.setChambre(chambre);

        residentService.save(resident);
        chambreService.save(chambre);

        return "redirect:/chambres/" + id;
    }

    @PostMapping("/{id}/liberer")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String liberer(@PathVariable Integer id) {

        Chambre chambre = chambreService.findById(id).orElseThrow();

        Resident occupant = chambre.getOccupant();
        if (occupant == null) {
            return "redirect:/chambres/" + id + "?error=dejaLibre";
        }

        occupant.setChambre(null);
        chambre.setOccupant(null);

        residentService.save(occupant);
        chambreService.save(chambre);

        return "redirect:/chambres/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String delete(@PathVariable Integer id) {

        if (chambreService.isOccupee(id)) {
            return "redirect:/chambres/" + id + "?error=occupee";
        }

        chambreService.delete(id);
        return "redirect:/chambres";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String editForm(@PathVariable Integer id, Model model) {

        Chambre chambre = chambreService.findById(id).orElseThrow();

        model.addAttribute("chambre", chambre);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/chambres/" + id);
        model.addAttribute("activePage", "chambres"); // 👈

        return "chambres/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
    public String update(@PathVariable Integer id,
                        @Valid @ModelAttribute Chambre chambre,
                        BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);                    
            model.addAttribute("submitUrl", "/chambres/" + id);
            return "chambres/form";
        }

        chambreService.update(id, chambre);
        return "redirect:/chambres/" + id;
    }

}