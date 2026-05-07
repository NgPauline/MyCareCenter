package com.example.demo.init;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ChambreService;
import com.example.demo.service.EmployeService;
import com.example.demo.service.ResidentService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeRepository employeRepository;
    private final EmployeService employeService; 
    private final SoignantRepository soignantRepository;
    private final AdministratifRepository administratifRepository;
    private final ResidentRepository residentRepository;
    private final ChambreRepository chambreRepository;
    private final FamilleRepository familleRepository;
    private final ActiviteRepository activiteRepository;
    private final ConsultationRepository consultationRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final TraitementRepository traitementRepository;
    private final PlanningRepository planningRepository;
    private final EquipementRepository equipementRepository;
    private final TypeEquipementRepository typeEquipementRepository;

    private final ResidentService residentService;
    private final ChambreService chambreService;
    private final PasswordEncoder passwordEncoder;

public DataInitializer(
                EmployeRepository employeRepository,
                EmployeService employeService,
                SoignantRepository soignantRepository,
                AdministratifRepository administratifRepository,
                ResidentRepository residentRepository,
                ResidentService residentService,
                ChambreRepository chambreRepository,
                ChambreService chambreService,
                FamilleRepository familleRepository,
                ActiviteRepository activiteRepository,
                ConsultationRepository consultationRepository,
                FactureRepository factureRepository,
                PaiementRepository paiementRepository,
                DossierMedicalRepository dossierMedicalRepository,
                TraitementRepository traitementRepository,
                PlanningRepository planningRepository,
                EquipementRepository equipementRepository,
                TypeEquipementRepository typeEquipementRepository,   // ⭐ AJOUT ICI
                PasswordEncoder passwordEncoder
        ) {
        this.employeRepository = employeRepository;
        this.employeService = employeService;
        this.soignantRepository = soignantRepository;
        this.administratifRepository = administratifRepository;
        this.residentRepository = residentRepository;
        this.residentService = residentService;
        this.chambreRepository = chambreRepository;
        this.chambreService = chambreService;
        this.familleRepository = familleRepository;
        this.activiteRepository = activiteRepository;
        this.consultationRepository = consultationRepository;
        this.factureRepository = factureRepository;
        this.paiementRepository = paiementRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.traitementRepository = traitementRepository;
        this.planningRepository = planningRepository;
        this.equipementRepository = equipementRepository;
        this.typeEquipementRepository = typeEquipementRepository; // ⭐ AJOUT ICI
        this.passwordEncoder = passwordEncoder;
        }



    @Override
    public void run(String... args) {

        // ------------------------------------------------------------
        // 1. CHAMBRES
        // ------------------------------------------------------------
        Chambre ch1 = chambreService.save(new Chambre(null, "Normale", 1));
        Chambre ch2 = chambreService.save(new Chambre(null, "PMR", 1));
        Chambre ch3 = chambreService.save(new Chambre(null, "Normale", 2));
        Chambre ch4 = chambreService.save(new Chambre(null, "PMR", 2));
        Chambre ch5 = chambreService.save(new Chambre(null, "Normale", 3));
        Chambre ch6 = chambreService.save(new Chambre(null, "PMR", 3));
        Chambre ch7 = chambreService.save(new Chambre(null, "Normale", 1));
        Chambre ch8 = chambreService.save(new Chambre(null, "PMR", 2));



        TypeEquipement fauteuil = typeEquipementRepository.save(
        new TypeEquipement("Fauteuil roulant",  "fauteuil-roulant.png", 10)
        );

        TypeEquipement deambulateur = typeEquipementRepository.save(
                new TypeEquipement("Déambulateur",  "déambulateur.jpg", 8)
        );

        TypeEquipement lit = typeEquipementRepository.save(
                new TypeEquipement("Lit médicalisé", "lit-médicalisé.jpg", 20)
        );

        TypeEquipement tensiometre = typeEquipementRepository.save(
                new TypeEquipement("Tensiomètre", "tensiomètre.jpg", 15)
        );

        TypeEquipement chaiseDouche = typeEquipementRepository.save(
                new TypeEquipement("Chaise de douche",  "chaise-de-douche.jpg", 12)
        );

        TypeEquipement tableSoins = typeEquipementRepository.save(
                new TypeEquipement("Table de soins",  "table-de-soins.jpg", 5)
        );

        TypeEquipement ambuBag = typeEquipementRepository.save(
                new TypeEquipement("Ambu-bag",  "ambu-bag.jpg", 7)
        );

        TypeEquipement lampe = typeEquipementRepository.save(
                new TypeEquipement("Lampe médicale",  "lampe-medicale.jpg", 9)
        );

        // ------------------------------------------------------------
        // 3. ÉQUIPEMENTS (1 par chambre)
        // ------------------------------------------------------------
        equipementRepository.save(new Equipement(fauteuil, "BON", "fauteuil-roulant.png", ch1));
        equipementRepository.save(new Equipement(deambulateur, "USÉ", "déambulateur.jpg", ch2));
        equipementRepository.save(new Equipement(lit, "EXCELLENT", "lit-médicalisé.jpg", ch3));
        equipementRepository.save(new Equipement(tensiometre, "BON", "tensiomètre.jpg", ch4));
        equipementRepository.save(new Equipement(chaiseDouche, "BON", "chaise-de-douche.jpg", ch5));
        equipementRepository.save(new Equipement(tableSoins, "MOYEN", "table-de-soins.jpg", ch6));
        equipementRepository.save(new Equipement(ambuBag, "EXCELLENT", "ambu-bag.jpg", ch7));
        equipementRepository.save(new Equipement(lampe, "BON", "lampe-medicale.jpg", ch8));

        // ------------------------------------------------------------
        // 3. FAMILLES
        // ------------------------------------------------------------
        Famille fam1 = familleRepository.save(new Famille("Durand", "Paul", "Fils", "Proche", "+32477001111", "fam1@mail.com", "Rue Famille 1, 1000 Bruxelles"));
        Famille fam2 = familleRepository.save(new Famille("Martin", "Claire", "Fille", "Proche", "+32477002222", "fam2@mail.com", "Rue Famille 2, 1000 Bruxelles"));
        Famille fam3 = familleRepository.save(new Famille("Bernard", "Luc", "Frère", "Proche", "+32477003333", "fam3@mail.com", "Rue Famille 3, 1000 Bruxelles"));
        Famille fam4 = familleRepository.save(new Famille("Petit", "Julie", "Sœur", "Proche", "+32477004444", "fam4@mail.com", "Rue Famille 4, 1000 Bruxelles"));
        Famille fam5 = familleRepository.save(new Famille("Robert", "Hugo", "Fils", "Proche", "+32477005555", "fam5@mail.com", "Rue Famille 5, 1000 Bruxelles"));
        Famille fam6 = familleRepository.save(new Famille("Moreau", "Emma", "Fille", "Proche", "+32477006666", "fam6@mail.com", "Rue Famille 6, 1000 Bruxelles"));
        Famille fam7 = familleRepository.save(new Famille("Fabre", "Louis", "Neveu", "Proche", "+32477007777", "fam7@mail.com", "Rue Famille 7, 1000 Bruxelles"));
        Famille fam8 = familleRepository.save(new Famille("Germain", "Alice", "Nièce", "Proche", "+32477008888", "fam8@mail.com", "Rue Famille 8, 1000 Bruxelles"));

        // ------------------------------------------------------------
        // 4. EMPLOYÉS (10 EXACTEMENT, ROLES HARMONISÉS)
        // ------------------------------------------------------------

        // 1. DIRECTEUR
        Employe directeur = new Employe(
                "Durand", "Marc", LocalDate.now().minusYears(50),
                "+32488001100", "Rue du Bureau 2, 1000 Bruxelles",
                null, "Directeur", 3500.0,
                passwordEncoder.encode("directeur123"),
                "DIRECTEUR"
        );
        employeService.save(directeur);

        // 2. ADMINISTRATIFS (3)
        Administratif admin1 = new Administratif(
                "Lemaire", "Sophie", LocalDate.now().minusYears(35),
                "+32488002200", "Rue du Bureau 3, 1000 Bruxelles",
                null, "Secrétaire", 2300.0,
                passwordEncoder.encode("secret123"),
                "ADMINISTRATIF",
                "Secrétaire"
        );
        employeService.save(admin1);

        Administratif admin2 = new Administratif(
                "Martin", "Luc", LocalDate.now().minusYears(40),
                "+32488003300", "Rue Finance 4, 1000 Bruxelles",
                null, "Gestionnaire administratif", 2600.0,
                passwordEncoder.encode("admin2"),
                "ADMINISTRATIF",
                "Gestion"
        );
        employeService.save(admin2);

        Administratif admin3 = new Administratif(
                "Bernard", "Julie", LocalDate.now().minusYears(30),
                "+32488004400", "Rue École 1, 1000 Bruxelles",
                null, "Assistante administrative", 2200.0,
                passwordEncoder.encode("admin3"),
                "ADMINISTRATIF",
                "Administration"
        );
        employeService.save(admin3);

        // 3. FINANCE
        Employe comptable = new Employe(
                "Petit", "Hugo", LocalDate.now().minusYears(38),
                "+32488005500", "Rue Finance 10, 1000 Bruxelles",
                null, "Comptable", 2700.0,
                passwordEncoder.encode("compta123"),
                "FINANCE"
        );
        employeService.save(comptable);

        // 4. EDUCATEURS (2)
        Employe educ1 = new Employe(
                "Robert", "Emma", LocalDate.now().minusYears(32),
                "+32488006600", "Rue École 3, 1000 Bruxelles",
                null , "Éducatrice", 2200.0,
                passwordEncoder.encode("educ1"),
                "EDUCATEUR"
        );
        employeService.save(educ1);

        Employe educ2 = new Employe(
                "Moreau", "Louis", LocalDate.now().minusYears(29),
                "+32488007700", "Rue École 4, 1000 Bruxelles",
                null, "Éducateur", 2200.0,
                passwordEncoder.encode("educ2"),
                "EDUCATEUR"
        );
        employeService.save(educ2);

        // 5. SOIGNANTS (3)
        Soignant soi1 = new Soignant(
                "Martin", "Claire", LocalDate.now().minusYears(32),
                "+32499001100", "Rue Santé 1, 1000 Bruxelles",
                null, "Infirmière", 2600.0,
                passwordEncoder.encode("soi001"),
                "SOIGNANT",
                "Diplôme Infirmier", "Gériatrie"
        );
        employeService.save(soi1);

        Soignant soi2 = new Soignant(
                "Leroy", "Paul", LocalDate.now().minusYears(34),
                "+32499002200", "Rue Santé 2, 1000 Bruxelles",
                null, "Infirmier", 2550.0,
                passwordEncoder.encode("soi002"),
                "SOIGNANT",
                "Diplôme Infirmier", "Urgences"
        );
        employeService.save(soi2);

        Soignant soi3 = new Soignant(
                "Morel", "Sarah", LocalDate.now().minusYears(29),
                "+32499003300", "Rue Santé 3, 1000 Bruxelles",
                null, "Aide-soignante", 2400.0,
                passwordEncoder.encode("soi003"),
                "SOIGNANT",
                "Certificat AS", "Gériatrie"
        );
        employeService.save(soi3);



        // ------------------------------------------------------------
        // 6. RESIDENTS + DOSSIERS MÉDICAUX
        // ------------------------------------------------------------

        // =========================
        //   RÉSIDENT 1
        // =========================
        Resident res1 = new Resident(
                "Leroy", "Marie",
                LocalDate.now().minusYears(30),
                "Diabète",
                "Autonome",
                LocalDate.now().minusMonths(2),
                "Actif"
        );
        res1.setChambre(ch1);
        res1.ajouterFamille(fam1);

        DossierMedical dm1 = new DossierMedical(
                LocalDate.now().minusMonths(2),
                "A+",
                "Pollen",
                "RAS"
        );
        res1.setDossierMedical(dm1);
        dm1.setResident(res1);

        residentService.save(res1);


        // =========================
        //   RÉSIDENT 2
        // =========================
        Resident res2 = new Resident(
                "Morel", "Jean",
                LocalDate.now().minusYears(40),
                "Hypertension",
                "Semi-autonome",
                LocalDate.now().minusMonths(3),
                "Actif"
        );
        res2.setChambre(ch2);
        res2.ajouterFamille(fam2);

        DossierMedical dm2 = new DossierMedical(
                LocalDate.now().minusMonths(3),
                "O-",
                "Arachides",
                "Surveillance"
        );
        res2.setDossierMedical(dm2);
        dm2.setResident(res2);

        residentService.save(res2);


        // =========================
        //   RÉSIDENT 3
        // =========================
        Resident res3 = new Resident(
                "Bernard", "Lucie",
                LocalDate.now().minusYears(27),
                "Arthrose",
                "Autonome",
                LocalDate.now().minusMonths(1),
                "Actif"
        );
        res3.setChambre(ch3);
        res3.ajouterFamille(fam3);

        DossierMedical dm3 = new DossierMedical(
                LocalDate.now().minusMonths(1),
                "B+",
                "Aucun",
                "RAS"
        );
        res3.setDossierMedical(dm3);
        dm3.setResident(res3);

        residentService.save(res3);


        // =========================
        //   RÉSIDENT 4
        // =========================
        Resident res4 = new Resident(
                "Petit", "Hélène",
                LocalDate.now().minusYears(34),
                "Insuffisance cardiaque",
                "Dépendant",
                LocalDate.now().minusMonths(4),
                "Actif"
        );
        res4.setChambre(ch4);
        res4.ajouterFamille(fam4);

        DossierMedical dm4 = new DossierMedical(
                LocalDate.now().minusMonths(4),
                "AB-",
                "Pénicilline",
                "Fragile"
        );
        res4.setDossierMedical(dm4);
        dm4.setResident(res4);

        residentService.save(res4);


        // =========================
        //   RÉSIDENT 5
        // =========================
        Resident res5 = new Resident(
                "Robert", "Paul",
                LocalDate.now().minusYears(38),
                "Parkinson",
                "Semi-autonome",
                LocalDate.now().minusMonths(5),
                "Actif"
        );
        res5.setChambre(ch5);
        res5.ajouterFamille(fam5);

        DossierMedical dm5 = new DossierMedical(
                LocalDate.now().minusMonths(5),
                "A-",
                "Aucun",
                "RAS"
        );
        res5.setDossierMedical(dm5);
        dm5.setResident(res5);

        residentService.save(res5);


        // =========================
        //   RÉSIDENT 6
        // =========================
        Resident res6 = new Resident(
                "Moreau", "Alice",
                LocalDate.now().minusYears(50),
                "Alzheimer",
                "Dépendant",
                LocalDate.now().minusMonths(6),
                "Actif"
        );
        res6.setChambre(ch6);
        res6.ajouterFamille(fam6);

        DossierMedical dm6 = new DossierMedical(
                LocalDate.now().minusMonths(6),
                "O+",
                "Poussière",
                "Surveillance"
        );
        res6.setDossierMedical(dm6);
        dm6.setResident(res6);

        residentService.save(res6);


        // =========================
        //   RÉSIDENT 7
        // =========================
        Resident res7 = new Resident(
                "Fabre", "Louis",
                LocalDate.now().minusYears(47),
                "Insuffisance rénale",
                "Semi-autonome",
                LocalDate.now().minusMonths(7),
                "Actif"
        );
        res7.setChambre(ch7);
        res7.ajouterFamille(fam7);

        DossierMedical dm7 = new DossierMedical(
                LocalDate.now().minusMonths(7),
                "B-",
                "Aucun",
                "RAS"
        );
        res7.setDossierMedical(dm7);
        dm7.setResident(res7);

        residentService.save(res7);


        // =========================
        //   RÉSIDENT 8
        // =========================
        Resident res8 = new Resident("Germain", "Emma",LocalDate.now().minusYears(32),"Asthme sévère",
                "Autonome",
                LocalDate.now().minusMonths(8),
                "Actif"
        );
        res8.setChambre(ch8);
        res8.ajouterFamille(fam8);

        DossierMedical dm8 = new DossierMedical(
                LocalDate.now().minusMonths(8),
                "AB+",
                "Aucun",
                "RAS"
        );
        res8.setDossierMedical(dm8);
        dm8.setResident(res8);

        residentService.save(res8);



        // ------------------------------------------------------------
        // 9. TRAITEMENTS
        // ------------------------------------------------------------
        Traitement t1 = new Traitement("Doliprane", "500mg", "2x/jour",
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20));
        dm1.ajouterTraitement(t1);
        traitementRepository.save(t1);

        Traitement t2 = new Traitement("Ibuprofène", "200mg", "1x/jour",
                LocalDate.now().minusDays(12), LocalDate.now().plusDays(18));
        dm2.ajouterTraitement(t2);
        traitementRepository.save(t2);

        Traitement t3 = new Traitement("Metformine", "850mg", "2x/jour",
                LocalDate.now().minusDays(8), LocalDate.now().plusDays(25));
        dm3.ajouterTraitement(t3);
        traitementRepository.save(t3);

        Traitement t4 = new Traitement("Lévothyrox", "75µg", "1x/jour",
                LocalDate.now().minusDays(15), LocalDate.now().plusDays(30));
        dm4.ajouterTraitement(t4);
        traitementRepository.save(t4);

        Traitement t5 = new Traitement("Amlodipine", "10mg", "1x/jour",
                LocalDate.now().minusDays(20), LocalDate.now().plusDays(15));
        dm5.ajouterTraitement(t5);
        traitementRepository.save(t5);

        Traitement t6 = new Traitement("Donepezil", "5mg", "1x/jour",
                LocalDate.now().minusDays(25), LocalDate.now().plusDays(10));
        dm6.ajouterTraitement(t6);
        traitementRepository.save(t6);

        Traitement t7 = new Traitement("Furosémide", "40mg", "1x/jour",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(22));
        dm7.ajouterTraitement(t7);
        traitementRepository.save(t7);

        Traitement t8 = new Traitement("Ventoline", "2 bouffées", "Selon besoin",
                LocalDate.now().minusDays(3), LocalDate.now().plusDays(40));
        dm8.ajouterTraitement(t8);
        traitementRepository.save(t8);


                
        // ------------------------------------------------------------
        // 10. ACTIVITÉS
        // ------------------------------------------------------------

        // ACTIVITÉ 1
        Activite act1 = new Activite(
                LocalDate.now().plusDays(7),
                60,
                "Salle 1",
                "Gym douce",
                LocalTime.of(10, 0)
        );
        act1.setNom("Gym Senior");
        act1.setResponsable(soi1);
        act1.setCategorie(CategorieActivite.SPORTIF);

        // ACTIVITÉ 2
        Activite act2 = new Activite(
                LocalDate.now().plusDays(14),
                45,
                "Salle 2",
                "Atelier mémoire",
                LocalTime.of(14, 0)
        );
        act2.setNom("Mémoire Plus");
        act2.setResponsable(soi2);
        act2.setCategorie(CategorieActivite.EDUCATIF);

        // ACTIVITÉ 3
        Activite act3 = new Activite(
                LocalDate.now().plusDays(7),
                90,
                "Salle 3",
                "Art thérapie",
                LocalTime.of(9, 0)
        );
        act3.setNom("Peinture Relax");
        act3.setResponsable(soi3);
        act3.setCategorie(CategorieActivite.SOCIAL);

        // ACTIVITÉ 4
        Activite act4 = new Activite(
                LocalDate.now().plusDays(8),
                60,
                "Salle 4",
                "Musique",
                LocalTime.of(11, 0)
        );
        act4.setNom("Chorale");
        act4.setResponsable(soi3);
        act4.setCategorie(CategorieActivite.SOCIAL);

        // ACTIVITÉ 5
        Activite act5 = new Activite(
                LocalDate.now().plusDays(9),
                30,
                "Salle 5",
                "Lecture",
                LocalTime.of(15, 0)
        );
        act5.setNom("Lecture Zen");
        act5.setResponsable(soi2);
        act5.setCategorie(CategorieActivite.EDUCATIF);

        // ACTIVITÉ 6
        Activite act6 = new Activite(
                LocalDate.now().plusDays(8),
                50,
                "Salle 6",
                "Yoga",
                LocalTime.of(16, 0)
        );
        act6.setNom("Yoga Doux");
        act6.setResponsable(soi1);
        act6.setCategorie(CategorieActivite.SPORTIF);

        // ACTIVITÉ 7
        Activite act7 = new Activite(
                LocalDate.now().plusDays(9),
                40,
                "Salle 7",
                "Jeux de société",
                LocalTime.of(13, 0)
        );
        act7.setNom("Jeux & Sourires");
        act7.setResponsable(soi3);
        act7.setCategorie(CategorieActivite.SOCIAL);

        // ACTIVITÉ 8
        Activite act8 = new Activite(
                LocalDate.now().plusDays(10),
                70,
                "Salle 8",
                "Danse",
                LocalTime.of(17, 0)
        );
        act8.setNom("Danse Senior");
        act8.setResponsable(soi2);
        act8.setCategorie(CategorieActivite.SPORTIF);


        // ------------------------------------------------------------
        // SAUVEGARDE DES ACTIVITÉS (AVANT PARTICIPANTS & PLANNINGS)
        // ------------------------------------------------------------
        activiteRepository.save(act1);
        activiteRepository.save(act2);
        activiteRepository.save(act3);
        activiteRepository.save(act4);
        activiteRepository.save(act5);
        activiteRepository.save(act6);
        activiteRepository.save(act7);
        activiteRepository.save(act8);


        // ------------------------------------------------------------
        // AJOUT DES PARTICIPANTS (APRÈS SAUVEGARDE)
        // ------------------------------------------------------------
        act1.ajouterParticipant(res1);
        act1.ajouterParticipant(res2);

        act2.ajouterParticipant(res3);
        act2.ajouterParticipant(res4);

        act3.ajouterParticipant(res5);
        act3.ajouterParticipant(res6);

        act4.ajouterParticipant(res7);
        act4.ajouterParticipant(res8);

        act5.ajouterParticipant(res5);
        act5.ajouterParticipant(res3);

        act6.ajouterParticipant(res4);
        act6.ajouterParticipant(res3);

        // Mise à jour après ajout participants
        activiteRepository.save(act1);
        activiteRepository.save(act2);
        activiteRepository.save(act3);
        activiteRepository.save(act4);
        activiteRepository.save(act5);
        activiteRepository.save(act6);



        // ------------------------------------------------------------
        // 11. CONSULTATIONS
        // ------------------------------------------------------------

        Consultation c1 = new Consultation(LocalDateTime.now().plusDays(1), "Fatigue", "RAS", res1, soi1, dm1);
        res1.getConsultations().add(c1);
        dm1.getConsultations().add(c1);
        consultationRepository.save(c1);

        Consultation c2 = new Consultation(LocalDateTime.now().plusDays(2), "Douleurs articulaires", "Surveillance", res2, soi2, dm2);
        res2.getConsultations().add(c2);
        dm2.getConsultations().add(c2);
        consultationRepository.save(c2);

        Consultation c3 = new Consultation(LocalDateTime.now().plusDays(3), "Tension élevée", "Traitement ajusté", res3, soi3, dm3);
        res3.getConsultations().add(c3);
        dm3.getConsultations().add(c3);
        consultationRepository.save(c3);

        Consultation c4 = new Consultation(LocalDateTime.now().plusDays(4), "Essoufflement", "Contrôle nécessaire", res4, soi1, dm4);
        res4.getConsultations().add(c4);
        dm4.getConsultations().add(c4);
        consultationRepository.save(c4);

        Consultation c5 = new Consultation(LocalDateTime.now().plusDays(5), "Tremblements", "RAS", res5, soi2, dm5);
        res5.getConsultations().add(c5);
        dm5.getConsultations().add(c5);
        consultationRepository.save(c5);

        Consultation c6 = new Consultation(LocalDateTime.now().plusDays(6), "Perte de mémoire", "Suivi Alzheimer", res6, soi3, dm6);
        res6.getConsultations().add(c6);
        dm6.getConsultations().add(c6);
        consultationRepository.save(c6);

        Consultation c7 = new Consultation(LocalDateTime.now().plusDays(7), "Douleurs lombaires", "Physiothérapie", res7, soi1, dm7);
        res7.getConsultations().add(c7);
        dm7.getConsultations().add(c7);
        consultationRepository.save(c7);

        Consultation c8 = new Consultation(LocalDateTime.now().plusDays(8), "Crises d’asthme", "Traitement renforcé", res8, soi2, dm8);
        res8.getConsultations().add(c8);
        dm8.getConsultations().add(c8);
        consultationRepository.save(c8);

        // ------------------------------------------------------------
        // 12. FACTURES
        // ------------------------------------------------------------

        Facture fac1 = new Facture(LocalDate.now().minusDays(5), 150.0, "EN_ATTENTE", res1);
        factureRepository.save(fac1);
        res1.getFactures().add(fac1);

        Facture fac2 = new Facture(LocalDate.now().minusDays(6), 180.0, "EN_ATTENTE", res2);
        factureRepository.save(fac2);
        res2.getFactures().add(fac2);

        Facture fac3 = new Facture(LocalDate.now().minusDays(7), 200.0, "EN_ATTENTE", res3);
        factureRepository.save(fac3);
        res3.getFactures().add(fac3);

        Facture fac4 = new Facture(LocalDate.now().minusDays(8), 170.0, "EN_ATTENTE", res4);
        factureRepository.save(fac4);
        res4.getFactures().add(fac4);

        Facture fac5 = new Facture(LocalDate.now().minusDays(9), 160.0, "EN_ATTENTE", res5);
        factureRepository.save(fac5);
        res5.getFactures().add(fac5);

        Facture fac6 = new Facture(LocalDate.now().minusDays(10), 210.0, "EN_ATTENTE", res6);
        factureRepository.save(fac6);
        res6.getFactures().add(fac6);

        Facture fac7 = new Facture(LocalDate.now().minusDays(11), 190.0, "EN_ATTENTE", res7);
        factureRepository.save(fac7);
        res7.getFactures().add(fac7);

        Facture fac8 = new Facture(LocalDate.now().minusDays(12), 220.0, "EN_ATTENTE", res8);
        factureRepository.save(fac8);
        res8.getFactures().add(fac8);

        // ------------------------------------------------------------
        // 13. PAIEMENTS
        // ------------------------------------------------------------

        Paiement pay1 = new Paiement(LocalDate.now().minusDays(3), 50.0, "CB", fac1);
        pay1.validerPaiement();
        fac1.ajouterPaiement(pay1);
        paiementRepository.save(pay1);
        factureRepository.save(fac1);

        Paiement pay2 = new Paiement(LocalDate.now().minusDays(4), 60.0, "CB", fac2);
        pay2.validerPaiement();
        fac2.ajouterPaiement(pay2);
        paiementRepository.save(pay2);
        factureRepository.save(fac2);

        Paiement pay3 = new Paiement(LocalDate.now().minusDays(5), 70.0, "CB", fac3);
        pay3.validerPaiement();
        fac3.ajouterPaiement(pay3);
        paiementRepository.save(pay3);
        factureRepository.save(fac3);

        Paiement pay4 = new Paiement(LocalDate.now().minusDays(6), 80.0, "CB", fac4);
        pay4.validerPaiement();
        fac4.ajouterPaiement(pay4);
        paiementRepository.save(pay4);
        factureRepository.save(fac4);

        Paiement pay5 = new Paiement(LocalDate.now().minusDays(7), 90.0, "CB", fac5);
        pay5.validerPaiement();
        fac5.ajouterPaiement(pay5);
        paiementRepository.save(pay5);
        factureRepository.save(fac5);

        Paiement pay6 = new Paiement(LocalDate.now().minusDays(8), 100.0, "CB", fac6);
        pay6.validerPaiement();
        fac6.ajouterPaiement(pay6);
        paiementRepository.save(pay6);
        factureRepository.save(fac6);

        Paiement pay7 = new Paiement(LocalDate.now().minusDays(9), 110.0, "CB", fac7);
        pay7.validerPaiement();
        fac7.ajouterPaiement(pay7);
        paiementRepository.save(pay7);
        factureRepository.save(fac7);

        Paiement pay8 = new Paiement(LocalDate.now().minusDays(10), 120.0, "CB", fac8);
        pay8.validerPaiement();
        fac8.ajouterPaiement(pay8);
        paiementRepository.save(pay8);
        factureRepository.save(fac8);


        // ------------------------------------------------------------
        // 14. PLANNINGS 
        // ------------------------------------------------------------

        // PLANNING 1
        Planning p1 = new Planning(
                LocalDate.now().plusDays(7),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p1.ajouterActivite(act1);
        p1.setResponsable(soi1);
        planningRepository.save(p1);

        // PLANNING 2
        Planning p2 = new Planning(
                LocalDate.now().plusDays(14),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p2.ajouterActivite(act2);
        p2.setResponsable(educ2);
        planningRepository.save(p2);

        // PLANNING 3
        Planning p3 = new Planning(
                LocalDate.now().plusDays(7),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p3.ajouterActivite(act3);
        p3.setResponsable(educ1);
        planningRepository.save(p3);

        // PLANNING 4
        Planning p4 = new Planning(
                LocalDate.now().plusDays(8),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p4.ajouterActivite(act4);
        p4.setResponsable(educ2);
        planningRepository.save(p4);

        // PLANNING 5
        Planning p5 = new Planning(
                LocalDate.now().plusDays(9),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p5.ajouterActivite(act5);
        p5.setResponsable(soi1);
        planningRepository.save(p5);

        // PLANNING 6
        Planning p6 = new Planning(
                LocalDate.now().plusDays(8),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p6.ajouterActivite(act6);
        p6.setResponsable(soi2);
        planningRepository.save(p6);

        // PLANNING 7
        Planning p7 = new Planning(
                LocalDate.now().plusDays(9),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p7.ajouterActivite(act7);
        p7.setResponsable(soi3);
        planningRepository.save(p7);

        // PLANNING 8
        Planning p8 = new Planning(
                LocalDate.now().plusDays(10),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        p8.ajouterActivite(act8);
        p8.setResponsable(educ1);
        planningRepository.save(p8);

    }
}

