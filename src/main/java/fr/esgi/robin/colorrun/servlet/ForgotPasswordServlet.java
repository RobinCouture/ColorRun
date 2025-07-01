package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import fr.esgi.robin.colorrun.util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "forgotPassword", value = "/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateUtil.processTemplate("forgot-password", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            TemplateUtil.processTemplate("forgot-password", req, resp, 
                Map.of("errorMessage", "L'email est obligatoire"));
            return;
        }

        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email.trim());
            
            if (utilisateur != null) {
                // Générer un token de réinitialisation
                String resetToken = UUID.randomUUID().toString();
                Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);
                
                // Sauvegarder le token et sa date d'expiration
                utilisateur.setResetToken(resetToken);
                utilisateur.setResetTokenExpiration(expirationTime);
                utilisateurRepository.update(utilisateur);
                
                // Construire l'URL de réinitialisation
                String resetUrl = req.getScheme() + "://" + req.getServerName() + 
                                 ":" + req.getServerPort() + req.getContextPath() + 
                                 "/reset-password?token=" + resetToken;
                
                // Envoyer l'email (simulation)
                boolean emailSent = EmailUtil.sendPasswordResetEmail(email, resetUrl, utilisateur.getPrenom());
                
                if (emailSent) {
                    TemplateUtil.processTemplate("forgot-password", req, resp, 
                        Map.of("successMessage", "Un email de réinitialisation a été envoyé à votre adresse"));
                } else {
                    TemplateUtil.processTemplate("forgot-password", req, resp, 
                        Map.of("errorMessage", "Erreur lors de l'envoi de l'email. Veuillez réessayer."));
                }
            } else {
                // Pour des raisons de sécurité, on affiche le même message même si l'email n'existe pas
                TemplateUtil.processTemplate("forgot-password", req, resp, 
                    Map.of("successMessage", "Si votre email existe dans notre système, vous recevrez un lien de réinitialisation"));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la réinitialisation: " + e.getMessage());
            e.printStackTrace();
            TemplateUtil.processTemplate("forgot-password", req, resp, 
                Map.of("errorMessage", "Erreur technique. Veuillez réessayer plus tard."));
        }
    }
} 