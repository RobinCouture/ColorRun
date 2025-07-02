package fr.esgi.robin.colorrun.util;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Utilisateur;

import java.time.format.DateTimeFormatter;
import java.util.Random;

public class DossardGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Génère un numéro de dossard unique pour un participant
     * Format: CR-[ANNEE]-[ID_COURSE]-[NUMERO_SEQUENCE]
     * Exemple: CR-2024-5-001234
     */
    public static String generateDossard(Courses course, Utilisateur utilisateur) {
        // Obtenir l'année de la course
        String annee = course.getDateHeure()
            .atZone(java.time.ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy"));
        
        // Générer un numéro séquentiel basé sur l'ID utilisateur + timestamp + aléatoire
        int sequence = Math.abs((utilisateur.getId().hashCode() + 
                                (int)(System.currentTimeMillis() % Integer.MAX_VALUE) + 
                                random.nextInt(10000)) % 999999);
        
        // Formater avec des zéros en préfixe
        String numeroFormate = String.format("%06d", sequence);
        
        // Construire le dossard final
        return String.format("CR-%s-%d-%s", annee, course.getId(), numeroFormate);
    }
    
    /**
     * Valide le format d'un numéro de dossard
     */
    public static boolean isValidDossard(String dossard) {
        if (dossard == null || dossard.isEmpty()) {
            return false;
        }
        
        // Vérifier le format: CR-YYYY-ID-NNNNNN
        return dossard.matches("^CR-\\d{4}-\\d+-\\d{6}$");
    }
    
    /**
     * Extrait l'ID de la course depuis un numéro de dossard
     */
    public static Integer extractCourseId(String dossard) {
        if (!isValidDossard(dossard)) {
            return null;
        }
        
        String[] parts = dossard.split("-");
        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Génère un dossard de secours en cas de problème
     */
    public static String generateFallbackDossard(Utilisateur utilisateur) {
        long timestamp = System.currentTimeMillis();
        int userHash = Math.abs(utilisateur.getId().hashCode() % 10000);
        return String.format("CR-TEMP-%d-%04d", timestamp % 100000, userHash);
    }
} 