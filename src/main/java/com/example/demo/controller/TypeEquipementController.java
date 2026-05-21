package com.example.demo.controller;

import com.example.demo.model.TypeEquipement;
import com.example.demo.service.EquipementService;
import com.example.demo.service.TypeEquipementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/types-equipement")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
public class TypeEquipementController {

    private final TypeEquipementService typeService;
    private final EquipementService equipementService;

    public TypeEquipementController(TypeEquipementService typeService,
                                    EquipementService equipementService) {
        this.typeService = typeService;
        this.equipementService = equipementService;
    }

    // ---------------------------------------------------------
    // LISTE + STOCK UTILISÉ / RESTANT
    // ---------------------------------------------------------
    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                    Model model) {

        List<TypeEquipement> types = (q != null && !q.isBlank())
                ? typeService.search(q)
                : typeService.findAll();

        Map<Integer, Long> utilisesMap = new HashMap<>();
        Map<Integer, Long> restantsMap = new HashMap<>();

        for (TypeEquipement t : types) {
            utilisesMap.put(t.getId(), typeService.countUtilises(t.getId()));
            restantsMap.put(t.getId(), typeService.countRestants(t));
        }

        model.addAttribute("types", types);
        model.addAttribute("utilisesMap", utilisesMap);
        model.addAttribute("restantsMap", restantsMap);
        model.addAttribute("q", q);
        model.addAttribute("activePage", "types-equipement");

        return "types/list";
    }

    // ---------------------------------------------------------
    // FORMULAIRE AJOUT
    // ---------------------------------------------------------
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer chambreId, Model model) {
        model.addAttribute("typeEquipement", new TypeEquipement());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/types-equipement");
        model.addAttribute("chambreId", chambreId);
        model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");
        return "types/form";
    }

    // ---------------------------------------------------------
    // CRÉATION
    // ---------------------------------------------------------
    @PostMapping
    public String create(@Valid @ModelAttribute TypeEquipement typeEquipement,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile,
                         @RequestParam(required = false) Integer chambreId,
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambreId", chambreId);
            model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");
            return "types/form";
        }

        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/types/";
            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Files.copy(photoFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            typeEquipement.setPhotoPath(fileName);
        }

        typeService.save(typeEquipement);

        if (chambreId != null) return "redirect:/chambres/" + chambreId;
        return "redirect:/types-equipement";
    }

    // ---------------------------------------------------------
    // FORMULAIRE EDIT
    // ---------------------------------------------------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id,
                           @RequestParam(required = false) Integer chambreId,
                           Model model) {

        TypeEquipement type = typeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        model.addAttribute("typeEquipement", type);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/types-equipement/" + id);
        model.addAttribute("chambreId", chambreId);
        model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");

        return "types/form";
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute TypeEquipement typeForm,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile,
                         @RequestParam(required = false) Integer chambreId,
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambreId", chambreId);
            model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");
            return "types/form";
        }

        TypeEquipement existing = typeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/types/";
            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Files.copy(photoFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            existing.setPhotoPath(fileName);
        }

        existing.setNom(typeForm.getNom());
        existing.setQuantiteTotale(typeForm.getQuantiteTotale());

        typeService.save(existing);

        if (chambreId != null) return "redirect:/chambres/" + chambreId;
        return "redirect:/types-equipement";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @RequestParam(required = false) Integer chambreId,
                         Model model) {

        TypeEquipement type = typeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        model.addAttribute("type", type);
        model.addAttribute("exemplaires", equipementService.findByType(type));
        model.addAttribute("utilises", typeService.countUtilises(id));
        model.addAttribute("restants", typeService.countRestants(type));
        model.addAttribute("chambreId", chambreId);
        model.addAttribute("activePage", chambreId != null ? "chambres" : "types-equipement");

        return "types/detail";
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         @RequestParam(required = false) Integer chambreId) {
        typeService.findById(id).orElseThrow(() -> new RuntimeException("Type introuvable"));
        typeService.delete(id);
        if (chambreId != null) return "redirect:/chambres/" + chambreId;
        return "redirect:/types-equipement";
    }
}

