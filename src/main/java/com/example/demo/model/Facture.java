package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFacture;
 
    @NotNull(message = "La date est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEmission;

    @DecimalMin("0.0")
    private double montant;
    
    @Column(nullable = false)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Resident resident;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Paiement> paiements = new ArrayList<>();

    public Facture() {
    }

    public Facture(LocalDate dateEmission, double montant, String statut, Resident resident) {
        this.dateEmission = dateEmission;
        this.montant = montant;
        this.statut = statut;
        this.resident = resident;
    }

    public Integer getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(Integer idFacture) {
        this.idFacture = idFacture;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }

    // UML : calculerMontantTotal
    public double calculerMontantTotal() {
        return montant;
    }

    // UML : ajouterPaiement
    public void ajouterPaiement(Paiement paiement) {
        if (!paiements.contains(paiement)) {
            paiements.add(paiement);
            paiement.setFacture(this);
        }
    }

    @Override
    public String toString() {
        return "Facture " + idFacture + " — " 
            + resident.getNom() + " " + resident.getPrenom();
    }


    // UML : obtenirSoldeRestant
    public double getSoldeRestant() {
        double paye = paiements.stream()
                .filter(Paiement::isValide)
                .mapToDouble(Paiement::getMontant)
                .sum();
        return montant - paye;
    }

    // UML : modifierStatut
    public void modifierStatut(String statut) {
        this.statut = statut;
    }
    public void recalculerStatut() {
    double solde = getSoldeRestant();

    if (solde <= 0) {
        modifierStatut("PAYEE");
    } else if (solde < montant) {
        modifierStatut("PARTIELLE");
    } else {
        modifierStatut("EN_ATTENTE");
    }
}


public byte[] genererPDF() {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        // ============================
        // BANDEAU ORANGE
        // ============================
        content.setNonStrokingColor(255, 140, 0); // orange
        content.addRect(0, 780, PDRectangle.A4.getWidth(), 50);
        content.fill();

        // ============================
        // LOGO
        // ============================
        PDImageXObject logo = PDImageXObject.createFromFile(
                "src/main/resources/static/images/logo.png",
                document
        );

        // Taille du logo
        float logoWidth = 60;
        float logoHeight = 60;

        // Position du logo (dans le bandeau)
        content.drawImage(logo, 40, 775, logoWidth, logoHeight);

        // ============================
        // TITRE FACTURE
        // ============================
        content.beginText();
        content.setNonStrokingColor(0, 0, 0);
        content.setFont(PDType1Font.HELVETICA_BOLD, 26);
        content.newLineAtOffset(120, 790);
        content.showText("FACTURE N° " + idFacture);
        content.endText();

        // ============================
        // BLOC INFORMATIONS
        // ============================
        int infoY = 730;

        // Labels
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(40, infoY);
        content.showText("Date :");
        content.newLineAtOffset(0, -20);
        content.showText("Client :");
        content.newLineAtOffset(0, -20);
        content.showText("Adresse (famille) :");
        content.endText();

        // Valeurs
        Famille famille = resident.getFamilles().isEmpty() ? null : resident.getFamilles().get(0);
        String adresseFamille = (famille != null) ? famille.getAdresse() : "Non renseignée";

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(120, infoY);
        content.showText(String.valueOf(dateEmission));
        content.newLineAtOffset(0, -20);
        content.showText(resident.getNom() + " " + resident.getPrenom());
        content.newLineAtOffset(25, -20);
        content.showText(adresseFamille);
        content.endText();

        // ============================
        // TITRES DU TABLEAU
        // ============================
        int tableY = 600;

        content.setStrokingColor(0, 0, 0);
        content.setLineWidth(1);
        content.addRect(40, tableY - 20, 515, 35);
        content.stroke();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(50, tableY);
        content.showText("Description");
        content.newLineAtOffset(250, 0);
        content.showText("Prix unitaire");
        content.newLineAtOffset(80, 0);
        content.showText("Total HT");
        content.endText();

        // ============================
        // LIGNE DU TABLEAU
        // ============================
        content.addRect(40, tableY - 60, 515, 40);
        content.stroke();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(50, tableY - 40);
        content.showText("Hébergement et soins en centre d'accueil");
        content.newLineAtOffset(250, 0);
        content.showText(montant + " EUR");
        content.newLineAtOffset(80, 0);
        content.showText(montant + " EUR");
        content.endText();

        // ============================
        // TOTAL + TVA
        // ============================
        int totalY = 500;

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(400, totalY);
        content.showText("Total : " + montant + " EUR");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(400, totalY - 20);
        content.showText("TVA (0%) : 0 EUR");
        content.endText();

        // ============================
        // SIGNATURE
        // ============================
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(40, 300);
        content.showText("Signature : __________________________");
        content.endText();

        // ============================
        // TERMES & CONDITIONS
        // ============================
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
        content.newLineAtOffset(40, 250);
        content.showText("Termes & conditions : Cette facture est valable à compter de sa date d'émission.");
        content.endText();

        content.close();
        document.save(out);
        document.close();

        return out.toByteArray();

    } catch (Exception e) {
        throw new RuntimeException("Erreur lors de la génération du PDF", e);
    }
}


}
