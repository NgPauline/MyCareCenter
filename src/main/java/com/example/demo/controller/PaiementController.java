package com.example.demo.controller;

import com.example.demo.model.Paiement;
import com.example.demo.model.Facture;
import com.example.demo.service.PaiementService;
import com.example.demo.service.FactureService;
import com.example.demo.service.ResidentService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/paiements")
@PreAuthorize("hasAnyRole('DIRECTEUR','FINANCE')")
public class PaiementController {

    private final PaiementService paiementService;
    private final FactureService factureService;
    private final ResidentService residentService;

    public PaiementController(PaiementService paiementService,
                              FactureService factureService,
                              ResidentService residentService) {
        this.paiementService = paiementService;
        this.factureService = factureService;
        this.residentService = residentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer resident,
                    Model model) {

        if (resident != null) {
            model.addAttribute("paiements", paiementService.findByResidentId(resident));
            model.addAttribute("residentId", resident);
            model.addAttribute("activePage", "residents");
            return "paiements/list";
        }

        model.addAttribute("paiements", paiementService.findAll());
        model.addAttribute("activePage", "paiements");
        return "paiements/list";
    }


    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer factureId,
                            @RequestParam(required = false) Integer residentId,
                            @RequestParam(required = false) String from,
                            Model model) {

        Paiement paiement = new Paiement();

        if (factureId != null) {
            Facture facture = factureService.findById(factureId).orElseThrow();
            paiement.setFacture(facture);
            model.addAttribute("factureId", factureId);
        }

        if (residentId != null) {
            residentService.findById(residentId).ifPresent(r -> {
                model.addAttribute("factures",
                    factureService.findByResident(r).stream()
                        .filter(f -> f.getSoldeRestant() > 0)
                        .collect(java.util.stream.Collectors.toList())
                );
            });
            model.addAttribute("residentId", residentId);
        } else {
            model.addAttribute("factures", factureService.findAll());
        }

        model.addAttribute("paiement", paiement);
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/paiements");
        model.addAttribute("from", from);

        return "paiements/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Paiement paiement,
                         BindingResult bindingResult,
                         @RequestParam Integer factureId,
                         @RequestParam(required = false) String from,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("factures", factureService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("submitUrl", "/paiements");
            model.addAttribute("from", from);
            return "paiements/form";
        }

        Facture facture = factureService.findById(factureId).orElseThrow();

        if (!factureService.peutAjouterPaiement(facture, paiement.getMontant())) {
            return "redirect:/paiements/new?factureId=" + factureId + "&error=montant"
                   + (from != null ? "&from=" + from : "");
        }

        paiement.setFacture(facture);
        paiement.validerPaiement();

        paiementService.save(paiement);
        facture.ajouterPaiement(paiement);
        facture.recalculerStatut();
        factureService.save(facture);

        if (from != null && !from.isBlank()) return "redirect:" + from;
        return "redirect:/paiements";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @RequestParam(required = false) String from,
                         Model model) {

        Paiement paiement = paiementService.findById(id).orElseThrow();
        model.addAttribute("paiement", paiement);
        model.addAttribute("from", from);
        return "paiements/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id,
                           @RequestParam(required = false) String from,
                           Model model) {

        Paiement paiement = paiementService.findById(id).orElseThrow();

        model.addAttribute("paiement", paiement);
        model.addAttribute("factures", factureService.findAll());
        model.addAttribute("factureId", paiement.getFacture().getIdFacture());
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/paiements/" + id);
        model.addAttribute("from", from);

        return "paiements/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Paiement paiement,
                         BindingResult bindingResult,
                         @RequestParam Integer factureId,
                         @RequestParam(required = false) String from,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("factures", factureService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("submitUrl", "/paiements/" + id);
            model.addAttribute("from", from);
            return "paiements/form";
        }

        Paiement original = paiementService.findById(id).orElseThrow();
        Facture facture = factureService.findById(factureId).orElseThrow();

        original.setMontant(paiement.getMontant());
        original.setDatePaiement(paiement.getDatePaiement());
        original.setMode(paiement.getMode());
        original.setFacture(facture);

        paiementService.save(original);

        if (from != null && !from.isBlank()) return "redirect:" + from;
        return "redirect:/paiements/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         @RequestParam(required = false) String from) {

        Paiement paiement = paiementService.findById(id).orElseThrow();
        Facture facture = paiement.getFacture();

        paiementService.delete(id);
        facture.recalculerStatut();
        factureService.save(facture);

        if (from != null && !from.isBlank()) return "redirect:" + from;
        return "redirect:/paiements";
    }
}