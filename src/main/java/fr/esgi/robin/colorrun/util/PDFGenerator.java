package fr.esgi.robin.colorrun.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.image.ImageDataFactory;
import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Utilisateur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class PDFGenerator {

    private static final int QR_CODE_SIZE = 200;

    /**
     * Génère un PDF de dossard pour une inscription avec QR code
     */
    public static byte[] generateDossardPDF(Inscription inscription) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            // Créer le document PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Fonts
            PdfFont boldFont = PdfFontFactory.createFont();
            PdfFont regularFont = PdfFontFactory.createFont();
            
            Courses course = inscription.getCourse();
            Utilisateur participant = inscription.getUtilisateur();
            
            // Titre principal
            Paragraph title = new Paragraph("COLORRUN")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE)
                .setMarginBottom(20);
            document.add(title);
            
            // Numéro de dossard mis en évidence
            Paragraph dossardNumber = new Paragraph("DOSSARD N° " + inscription.getDossard())
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(ColorConstants.YELLOW)
                .setPadding(10)
                .setMarginBottom(20);
            document.add(dossardNumber);
            
            // Table avec les informations
            Table infoTable = new Table(2);
            infoTable.setWidth(500);
            
            // Informations du participant
            addTableRow(infoTable, "PARTICIPANT", "", boldFont);
            addTableRow(infoTable, "Nom :", participant.getNom().toUpperCase(), regularFont);
            addTableRow(infoTable, "Prénom :", participant.getPrenom(), regularFont);
            
            // Espaceur
            infoTable.addCell(new Cell(1, 2).add(new Paragraph(" ")).setBorder(null));
            
            // Informations de la course
            addTableRow(infoTable, "COURSE", "", boldFont);
            addTableRow(infoTable, "Nom :", course.getNomCourse(), regularFont);
            addTableRow(infoTable, "Date :", course.getDateHeure().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), regularFont);
            addTableRow(infoTable, "Lieu :", course.getLieu(), regularFont);
            addTableRow(infoTable, "Distance :", course.getDistance() + " km", regularFont);
            
            document.add(infoTable);
            
            // Générer et ajouter le QR code
            try {
                String qrCodeData = QRCodeGenerator.generateQRCodeData(
                    course.getId().toString(),
                    participant.getId().toString(),
                    inscription.getDossard()
                );
                
                byte[] qrCodeBytes = QRCodeGenerator.generateQRCodeImage(qrCodeData, QR_CODE_SIZE, QR_CODE_SIZE);
                
                Image qrCode = new Image(ImageDataFactory.create(qrCodeBytes));
                qrCode.setTextAlignment(TextAlignment.CENTER);
                qrCode.setMarginTop(20);
                
                Paragraph qrLabel = new Paragraph("QR Code de vérification")
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
                document.add(qrLabel);
                document.add(qrCode);
                
                // Mettre à jour le QR code dans l'inscription si ce n'est pas déjà fait
                if (inscription.getQrCode() == null || inscription.getQrCode().isEmpty()) {
                    inscription.setQrCode(qrCodeData);
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la génération du QR code: " + e.getMessage());
                // Continuer sans QR code en cas d'erreur
                document.add(new Paragraph("QR Code : " + (inscription.getQrCode() != null ? inscription.getQrCode() : "Non disponible"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));
            }
            
            // Instructions
            Paragraph instructionsTitle = new Paragraph("INSTRUCTIONS")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(30);
            document.add(instructionsTitle);
            
            document.add(new Paragraph("• Présentez-vous 30 minutes avant le départ"));
            document.add(new Paragraph("• Gardez ce dossard visible pendant toute la course"));
            document.add(new Paragraph("• En cas de problème, contactez l'organisation"));
            
            // Footer
            Paragraph footer = new Paragraph("Bon amusement et que la couleur soit avec vous !")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFont(boldFont)
                .setFontColor(ColorConstants.BLUE);
            document.add(footer);
            
            Paragraph website = new Paragraph("ColorRun - www.colorrun.fr")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setMarginTop(10);
            document.add(website);
            
            document.close();
            
            System.out.println("📄 PDF généré pour le dossard " + inscription.getDossard() + 
                             " - Taille: " + baos.size() + " bytes");
            
        } catch (Exception e) {
            throw new IOException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Ajoute une ligne à la table d'informations
     */
    private static void addTableRow(Table table, String label, String value, PdfFont font) {
        Cell labelCell = new Cell().add(new Paragraph(label).setFont(font)).setBorder(null);
        Cell valueCell = new Cell().add(new Paragraph(value).setFont(font)).setBorder(null);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    /**
     * Génère le nom de fichier pour le dossard PDF
     */
    public static String generatePDFFileName(Inscription inscription) {
        String courseName = inscription.getCourse().getNomCourse()
            .replaceAll("[^a-zA-Z0-9]", "_")
            .toLowerCase();
        String participantName = inscription.getUtilisateur().getNom()
            .replaceAll("[^a-zA-Z0-9]", "_")
            .toLowerCase();
        
        return String.format("dossard_%s_%s_%s.pdf", 
            courseName, 
            participantName, 
            inscription.getDossard().replace("-", "_"));
    }
    
    /**
     * Valide qu'une inscription peut générer un PDF
     */
    public static boolean canGeneratePDF(Inscription inscription) {
        return inscription != null &&
               inscription.getCourse() != null &&
               inscription.getUtilisateur() != null &&
               inscription.getDossard() != null &&
               !inscription.getDossard().isEmpty();
    }
} 