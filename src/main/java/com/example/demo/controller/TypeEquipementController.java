package com.example.demo.controller;

import com.example.demo.model.TypeEquipement;
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

    public TypeEquipementController(TypeEquipementService typeService) {
        this.typeService = typeService;
    }

    // ---------------------------------------------------------
    // LISTE + STOCK UTILISÉ / RESTANT
    // ---------------------------------------------------------
    @GetMapping
    public String list(Model model) {

        List<TypeEquipement> types = typeService.findAll();

        Map<Integer, Long> utilisesMap = new HashMap<>();
        Map<Integer, Long> restantsMap = new HashMap<>();

        for (TypeEquipement t : types) {
            long utilises = typeService.countUtilises(t.getId());
            long restants = typeService.countRestants(t);

            utilisesMap.put(t.getId(), utilises);
            restantsMap.put(t.getId(), restants);
        }

        model.addAttribute("types", types);
        model.addAttribute("utilisesMap", utilisesMap);
        model.addAttribute("restantsMap", restantsMap);
        model.addAttribute("activePage", "equipements");

        return "types/list";
    }

    // ---------------------------------------------------------
    // FORMULAIRE AJOUT
    // ---------------------------------------------------------
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("typeEquipement", new TypeEquipement());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/types-equipement");
        return "types/form";
    }

    // ---------------------------------------------------------
    // CRÉATION
    // ---------------------------------------------------------
    @PostMapping
    public String create(@Valid @ModelAttribute TypeEquipement typeEquipement,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile,
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "types/form";
        }

        // Upload photo
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
        return "redirect:/types-equipement";
    }

    // ---------------------------------------------------------
    // FORMULAIRE EDIT
    // ---------------------------------------------------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {

        TypeEquipement type = typeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        model.addAttribute("typeEquipement", type);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/types-equipement/" + id);

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
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "types/form";
        }

        TypeEquipement existing = typeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type introuvable"));

        // Upload photo si nouvelle image
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

        // Mise à jour des champs
        existing.setNom(typeForm.getNom());
        existing.setQuantiteTotale(typeForm.getQuantiteTotale());

        typeService.save(existing);

        return "redirect:/types-equipement";
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        typeService.findById(id).orElseThrow(() -> new RuntimeException("Type introuvable"));
        typeService.delete(id);
        return "redirect:/types-equipement";
    }
}
