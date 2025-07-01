package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.PasswordUtils;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@WebServlet(name = "resetPassword", value = "/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            TemplateUtil.processTemplate("login", req, resp, 
                Map.of("errorMessage", "Token de réinitialisation invalide"));
            return;
        }

        // Vérifier si le token existe et est encore valide
        Utilisateur utilisateur = utilisateurRepository.findByResetToken(token);
        
        if (utilisateur == null || utilisateur.getResetTokenExpiration() == null || 
            utilisateur.getResetTokenExpiration().isBefore(Instant.now())) {
            TemplateUtil.processTemplate("login", req, resp, 
                Map.of("errorMessage", "Token de réinitialisation expiré ou invalide"));
            return;
        }

        // Afficher le formulaire de réinitialisation
        TemplateUtil.processTemplate("reset-password", req, resp, 
            Map.of("token", token, "email", utilisateur.getEmail()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (token == null || token.trim().isEmpty()) {
            TemplateUtil.processTemplate("login", req, resp, 
                Map.of("errorMessage", "Token invalide"));
            return;
        }

        if (newPassword == null || newPassword.length() < 8) {
            TemplateUtil.processTemplate("reset-password", req, resp, 
                Map.of("token", token, "errorMessage", "Le mot de passe doit contenir au moins 8 caractères"));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            TemplateUtil.processTemplate("reset-password", req, resp, 
                Map.of("token", token, "errorMessage", "Les mots de passe ne correspondent pas"));
            return;
        }

        try {
            Utilisateur utilisateur = utilisateurRepository.findByResetToken(token);
            
            if (utilisateur == null || utilisateur.getResetTokenExpiration() == null || 
                utilisateur.getResetTokenExpiration().isBefore(Instant.now())) {
                TemplateUtil.processTemplate("login", req, resp, 
                    Map.of("errorMessage", "Token expiré ou invalide"));
                return;
            }

            // Mettre à jour le mot de passe
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            utilisateur.setMotDePasse(hashedPassword);
            utilisateur.setResetToken(null);
            utilisateur.setResetTokenExpiration(null);
            
            utilisateurRepository.update(utilisateur);

            TemplateUtil.processTemplate("login", req, resp, 
                Map.of("successMessage", "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter."));
                
        } catch (Exception e) {
            System.err.println("Erreur lors de la réinitialisation: " + e.getMessage());
            e.printStackTrace();
            TemplateUtil.processTemplate("reset-password", req, resp, 
                Map.of("token", token, "errorMessage", "Erreur technique. Veuillez réessayer."));
        }
    }
} 