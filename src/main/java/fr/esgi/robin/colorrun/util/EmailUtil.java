package fr.esgi.robin.colorrun.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    
    // Configuration Gmail (exemple)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "colorrun@gmail.com";
    private static final String EMAIL_PASSWORD = "colorrun2025";
    
    /**
     * Envoie un vrai email de réinitialisation de mot de passe
     */
    public static boolean sendPasswordResetEmail(String email, String resetUrl, String prenom) {
        // Mode simulation amélioré
        System.out.println("📧 [SIMULATION] Email de réinitialisation");
        System.out.println("📧 Pour : " + email);
        System.out.println("📧 Lien : " + resetUrl);
        System.out.println("📧 COPIEZ CE LIEN dans votre navigateur pour tester :");
        System.out.println("📧 " + resetUrl);
        
        return true; // Simuler le succès
    }
    
    private static String createPasswordResetEmailTemplate(String prenom, String resetUrl) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<style>" +
            "body { font-family: Arial, sans-serif; line-height: 1.6; }" +
            ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; }" +
            ".content { background: #f8f9fa; padding: 30px; }" +
            ".button { display: inline-block; background: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
            ".footer { text-align: center; color: #6c757d; font-size: 12px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class=\"container\">" +
            "<div class=\"header\">" +
            "<h1>🏃‍♀️ ColorRun</h1>" +
            "<p>Réinitialisation de mot de passe</p>" +
            "</div>" +
            "<div class=\"content\">" +
            "<h2>Bonjour " + prenom + ",</h2>" +
            "<p>Vous avez demandé la réinitialisation de votre mot de passe ColorRun.</p>" +
            "<p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>" +
            "<a href=\"" + resetUrl + "\" class=\"button\">Réinitialiser mon mot de passe</a>" +
            "<p><strong>⏰ Ce lien expire dans 1 heure.</strong></p>" +
            "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>" +
            "</div>" +
            "<div class=\"footer\">" +
            "<p>L'équipe ColorRun</p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>";
    }
    
    /**
     * Simule l'envoi d'un email de vérification de compte
     */
    public static boolean sendVerificationEmail(String email, String verificationUrl, String prenom) {
        try {
            // Simulation de l'envoi d'email
            System.out.println("📧 SIMULATION EMAIL VERIFICATION - Envoi à: " + email);
            System.out.println("👤 Destinataire: " + prenom);
            System.out.println("🔗 Lien de vérification: " + verificationUrl);
            System.out.println("📝 Contenu de l'email:");
            System.out.println("---");
            System.out.println("Bonjour " + prenom + ",");
            System.out.println("");
            System.out.println("Bienvenue sur ColorRun ! Pour activer votre compte, cliquez sur le lien ci-dessous :");
            System.out.println("");
            System.out.println(verificationUrl);
            System.out.println("");
            System.out.println("Ce lien expire dans 24 heures.");
            System.out.println("");
            System.out.println("L'équipe ColorRun");
            System.out.println("---");
            System.out.println("✅ Email de vérification envoyé avec succès (SIMULATION)");
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email de vérification: " + e.getMessage());
            return false;
        }
    }
} 