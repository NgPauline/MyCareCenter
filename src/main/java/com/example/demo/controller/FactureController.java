package com.example.demo.controller;

import com.example.demo.model.Facture;
import com.example.demo.model.Resident;
import com.example.demo.service.FactureService;
import com.example.demo.service.ResidentService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/factures")
@PreAuthorize("hasAnyRole('DIRECTEUR','FINANCE')")
public class FactureController {

    private final FactureService factureService;
    private final ResidentService residentService;

    public FactureController(FactureService factureService,
                             ResidentService residentService) {
        this.factureService = factureService;
        this.residentService = residentService;
    }

    /* LISTE */
    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                    @RequestParam(name = "q", required = false) String q,
                    @RequestParam(value = "page", defaultValue = "0") int page,
                    Model model) {

        if (resident != null) {
            Resident r = residentService.findById(resident).orElseThrow();

            // ✅ Recherche filtrée par résident
            var factures = (q != null && !q.isBlank())
                    ? factureService.searchByResident(r, q)
                    : factureService.findByResident(r);

            model.addAttribute("factures", factures);
            model.addAttribute("residentId", resident);
            model.addAttribute("q", q);
            model.addAttribute("activePage", "residents");
            return "factures/list";
        }

        int size = 4;
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = (q != null && !q.isBlank())
                ? factureService.search(q, pageable)
                : factureService.findAll(pageable);

        model.addAttribute("factures", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "factures");
        return "factures/list";
    }

    /* FORMULAIRE CREATION */
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer residentId, Model model) {

    Facture facture = new Facture();

    if (residentId != null) {
        residentService.findById(residentId).ifPresent(r -> {
            facture.setResident(r);
            model.addAttribute("residentPreRempli", r); // ← ajouter
        });
    }

        model.addAttribute("facture", new Facture());
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("residentId", residentId);
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/factures");
        model.addAttribute("activePage", "factures");

        return "factures/form";
    }

    /* CREATION */
    @PostMapping
    public String create(@Valid @ModelAttribute Facture facture,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/factures");
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("activePage", "factures");
            return "factures/form";
        }

        Resident resident = residentService.findById(residentId).orElseThrow();
        facture.setStatut("EN_ATTENTE");
        facture.setResident(resident);
        factureService.save(facture);

        return "redirect:/factures";
    }

    /* DETAIL */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Facture facture = factureService.findById(id).orElseThrow();
        model.addAttribute("facture", facture);
        model.addAttribute("paiements", facture.getPaiements());
        model.addAttribute("activePage", "factures");
        return "factures/detail";
    }

    /* FORMULAIRE EDITION */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {

        Facture facture = factureService.findById(id).orElseThrow();

        if (facture.getSoldeRestant() <= 0)
            return "redirect:/factures/" + id + "?error=payee";

        model.addAttribute("facture", facture);
        model.addAttribute("residents", residentService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/factures/" + id);
        model.addAttribute("activePage", "factures");

        return "factures/form";
    }

    /* EDITION */
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Facture facture,
                         BindingResult bindingResult,
                         @RequestParam Integer residentId,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/factures/" + id);
            model.addAttribute("residents", residentService.findAll());
            model.addAttribute("activePage", "factures");
            return "factures/form";
        }

        Facture original = factureService.findById(id).orElseThrow();

        if (original.getSoldeRestant() <= 0)
            return "redirect:/factures/" + id + "?error=payee";

        facture.setResident(original.getResident());
        factureService.update(id, facture);

        return "redirect:/factures/" + id;
    }

    /* SUPPRESSION */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        if (factureService.hasPaiements(id))
            return "redirect:/factures/" + id + "?error=paiements";

        factureService.delete(id);
        return "redirect:/factures";
    }

    /* PDF */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable Integer id) {

        Facture facture = factureService.findById(id).orElseThrow();
        byte[] pdfBytes = facture.genererPDF();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "facture_" + facture.getIdFacture() + ".pdf",
                "facture_" + facture.getIdFacture() + ".pdf"
        );

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
