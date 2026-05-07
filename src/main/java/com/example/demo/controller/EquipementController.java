package com.example.demo.controller;

import com.example.demo.model.Equipement;
import com.example.demo.model.TypeEquipement;
import com.example.demo.service.ChambreService;
import com.example.demo.service.EquipementService;
import com.example.demo.service.TypeEquipementService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/equipements")
@PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATIF')")
public class EquipementController {

    private final EquipementService equipementService;
    private final ChambreService chambreService;
    private final TypeEquipementService typeEquipementService;

    public EquipementController(EquipementService equipementService,
                                ChambreService chambreService,
                                TypeEquipementService typeEquipementService) {
        this.equipementService = equipementService;
        this.chambreService = chambreService;
        this.typeEquipementService = typeEquipementService;
    }

    // ---------------------------------------------------------
    // LISTE GÉNÉRALE + RECHERCHE + PAGINATION
    // ---------------------------------------------------------
    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer chambre,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        if (chambre != null) {
            model.addAttribute("equipements", equipementService.findByChambre(chambre));
            model.addAttribute("idChambre", chambre);
            model.addAttribute("activePage", "equipements");
            return "equipements/list-chambre";
        }

        int size = 6;
        Pageable pageable = PageRequest.of(page, size);

        var pageResult = (q != null && !q.isEmpty())
                ? equipementService.search(q, pageable)
                : equipementService.findAll(pageable);

        model.addAttribute("equipements", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("activePage", "equipements");

        return "equipements/list";
    }

    // ---------------------------------------------------------
    // LISTE PAR CHAMBRE
    // ---------------------------------------------------------
    @GetMapping("/chambre/{idChambre}")
    public String listByChambre(@PathVariable Integer idChambre, Model model) {
        model.addAttribute("equipements", equipementService.findByChambre(idChambre));
        model.addAttribute("idChambre", idChambre);
        model.addAttribute("activePage", "equipements");
        return "equipements/list-chambre";
    }

    // ---------------------------------------------------------
    // FORMULAIRE AJOUT
    // ---------------------------------------------------------
    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("equipement", new Equipement());
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/equipements");
        model.addAttribute("chambres", chambreService.findAll());
        model.addAttribute("types", typeEquipementService.findAll());
        model.addAttribute("activePage", "equipements");

        return "equipements/form";
    }

    // ---------------------------------------------------------
    // CRÉATION (AFFECTATION D’UN EXEMPLAIRE)
    // ---------------------------------------------------------
    @PostMapping
    public String create(@Valid @ModelAttribute Equipement equipement,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile,
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            return "equipements/form";
        }

        // Vérifier stock restant
        TypeEquipement type = equipement.getType();
        int total = type.getQuantiteTotale();
        long utilises = equipementService.countByType(type.getId());
        long restant = total - utilises;

        if (restant <= 0) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            model.addAttribute("errorStock", "Stock insuffisant pour ce type d'équipement.");
            return "equipements/form";
        }

        // Upload photo
        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/equipements/";
            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Files.copy(photoFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            equipement.setPhotoPath(fileName);
        }

        equipementService.save(equipement);
        return "redirect:/equipements";
    }

    // ---------------------------------------------------------
    // FORMULAIRE EDIT
    // ---------------------------------------------------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {

        Equipement equipement = equipementService.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable"));

        model.addAttribute("equipement", equipement);
        model.addAttribute("isEdit", true);
        model.addAttribute("submitUrl", "/equipements/" + id);
        model.addAttribute("chambres", chambreService.findAll());
        model.addAttribute("types", typeEquipementService.findAll());
        model.addAttribute("activePage", "equipements");

        return "equipements/form";
    }

    // ---------------------------------------------------------
    // UPDATE (NE PAS DÉPLACER / NE PAS CHANGER LE TYPE)
    // ---------------------------------------------------------
    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute Equipement equipementForm,
                         BindingResult bindingResult,
                         @RequestParam("photoFile") MultipartFile photoFile,
                         Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("chambres", chambreService.findAll());
            model.addAttribute("types", typeEquipementService.findAll());
            return "equipements/form";
        }

        Equipement existing = equipementService.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable"));

        // Interdire changement de type
        equipementForm.setType(existing.getType());

        // Interdire déplacement
        equipementForm.setChambre(existing.getChambre());

        // Upload photo
        if (!photoFile.isEmpty()) {
            String uploadDir = "uploads/equipements/";
            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            Files.copy(photoFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            existing.setPhotoPath(fileName);
        }

        // Mise à jour autorisée : état uniquement
        existing.setEtat(equipementForm.getEtat());

        equipementService.update(id, existing);

        return "redirect:/equipements";
    }

    // ---------------------------------------------------------
    // DELETE (LIBÈRE UN EXEMPLAIRE)
    // ---------------------------------------------------------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        equipementService.delete(id);
        return "redirect:/equipements";
    }

    // ---------------------------------------------------------
    // REDIRECTION
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    public String redirectToList() {
        return "redirect:/equipements";
    }
}
