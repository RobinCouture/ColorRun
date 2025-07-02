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

@WebServlet(name = "register", value = "/register")
public class RegisterServlet extends HttpServlet {
    private final UtilisateurRepository userRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateUtil.processTemplate("register", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");
        String confirmMotDePasse = req.getParameter("confirmMotDePasse");

        // Validations côté serveur
        if (nom == null || nom.trim().isEmpty() ||
            prenom == null || prenom.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            motDePasse == null || motDePasse.trim().isEmpty()) {
            TemplateUtil.processTemplate("register", req, resp, 
                Map.of("errorMessage", "Tous les champs sont obligatoires"));
            return; // IMPORTANT : arrêter l'exécution
        }

        if (motDePasse.length() < 8) {
            TemplateUtil.processTemplate("register", req, resp, 
                Map.of("errorMessage", "Le mot de passe doit contenir au moins 8 caractères"));
            return;
        }

        if (confirmMotDePasse != null && !motDePasse.equals(confirmMotDePasse)) {
            TemplateUtil.processTemplate("register", req, resp, 
                Map.of("errorMessage", "Les mots de passe ne correspondent pas"));
            return;
        }

        // Vérifier si l'email existe déjà
        Utilisateur userExist = userRepository.findByEmail(email);
        if (userExist != null) {
            TemplateUtil.processTemplate("register", req, resp, 
                Map.of("errorMessage", "Cet email est déjà utilisé"));
            return; // IMPORTANT : arrêter l'exécution
        }

        try {
            // Créer le nouvel utilisateur
            String hashedPassword = PasswordUtils.hashPassword(motDePasse);
            Utilisateur newUser = new Utilisateur(null, nom.trim(), prenom.trim(), 
                email.trim().toLowerCase(), hashedPassword, null, Instant.now());
            
            userRepository.create(newUser);
            
            // Rediriger vers la page de connexion avec message de succès
            TemplateUtil.processTemplate("login", req, resp, 
                Map.of("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter."));
                
        } catch (Exception e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
            TemplateUtil.processTemplate("register", req, resp, 
                Map.of("errorMessage", "Erreur lors de l'inscription. Veuillez réessayer."));
        }
    }
}
