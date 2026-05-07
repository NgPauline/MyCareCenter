package com.example.demo.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.example.demo.service.EquipementService;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ResidentService;
import com.example.demo.service.EmployeService;
import com.example.demo.service.FactureService;
import com.example.demo.service.ActiviteService;

@Controller
public class AuthController {

    private final ResidentService residentService;
    private final EmployeService employeService;
    private final FactureService factureService;
    private final ActiviteService activiteService;
    private final EquipementService equipementService; 
    

    public AuthController(ResidentService residentService,
                          EmployeService employeService,
                          FactureService factureService,
                          ActiviteService activiteService,
                          EquipementService equipementService) {
        this.residentService = residentService;
        this.employeService = employeService;
        this.factureService = factureService;
        this.activiteService = activiteService;
        this.equipementService = equipementService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("nbResidents", residentService.count());
        model.addAttribute("nbEmployes", employeService.count());
        model.addAttribute("nbFacturesEnAttente", factureService.countEnAttente());
        model.addAttribute("nbActivitesDuJour", activiteService.countToday());
        model.addAttribute("nbEquipementsTotal",      equipementService.count());
        model.addAttribute("nbEquipementsEnChambre",  equipementService.countEnChambre());

            // labels communs aux deux graphiques
        List<String> labels = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            labels.add(YearMonth.now().minusMonths(i)
                .format(DateTimeFormatter.ofPattern("MMM", Locale.FRENCH)));
        }
        model.addAttribute("chartLabels",        labels);
        model.addAttribute("facturesMontants",   factureService.getMonthlyAmounts(6));
        model.addAttribute("facturesPayees",     factureService.getMonthlyPaid(6));
        model.addAttribute("residentsData",      residentService.getMonthlyCount(6));

        return "dashboard/index";
    }
}
