package fr.esgi.robin.colorrun.util;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Utilisateur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class PDFGenerator {

    /**
     * Génère un PDF de dossard pour une inscription
     * Version simplifiée - génère du contenu texte pour simulation
     */
    public static byte[] generateDossardPDF(Inscription inscription) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            Courses course = inscription.getCourse();
            Utilisateur participant = inscription.getUtilisateur();
            
            // Créer le contenu du dossard en format texte simple
            StringBuilder pdfContent = new StringBuilder();
            pdfContent.append("=== COLORRUN DOSSARD ===\n\n");
            pdfContent.append("NUMÉRO DE DOSSARD: ").append(inscription.getDossard()).append("\n\n");
            
            pdfContent.append("=== PARTICIPANT ===\n");
            pdfContent.append("Nom: ").append(participant.getNom().toUpperCase()).append("\n");
            pdfContent.append("Prénom: ").append(participant.getPrenom()).append("\n\n");
            
            pdfContent.append("=== COURSE ===\n");
            pdfContent.append("Nom: ").append(course.getNomCourse()).append("\n");
            pdfContent.append("Date: ").append(course.getDateHeure().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))).append("\n");
            pdfContent.append("Lieu: ").append(course.getLieu()).append("\n");
            pdfContent.append("Distance: ").append(course.getDistance()).append(" km\n\n");
            
            if (inscription.getQrCode() != null && !inscription.getQrCode().isEmpty()) {
                pdfContent.append("=== QR CODE DE VÉRIFICATION ===\n");
                pdfContent.append(inscription.getQrCode()).append("\n\n");
            }
            
            pdfContent.append("=== INSTRUCTIONS ===\n");
            pdfContent.append("• Présentez-vous 30 minutes avant le départ\n");
            pdfContent.append("• Gardez ce dossard visible pendant toute la course\n");
            pdfContent.append("• En cas de problème, contactez l'organisation\n\n");
            
            pdfContent.append("Bon amusement et que la couleur soit avec vous !\n");
            pdfContent.append("---\n");
            pdfContent.append("ColorRun - www.colorrun.fr\n");
            
            // Pour l'instant, on simule en retournant le contenu en bytes
            // Dans une vraie implémentation, on utiliserait iText pour créer un vrai PDF
            System.out.println("📄 SIMULATION PDF GÉNÉRÉ:");
            System.out.println(pdfContent.toString());
            System.out.println("📄 FIN SIMULATION PDF");
            
            baos.write(pdfContent.toString().getBytes("UTF-8"));
            
        } catch (Exception e) {
            throw new IOException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
        
        return baos.toByteArray();
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