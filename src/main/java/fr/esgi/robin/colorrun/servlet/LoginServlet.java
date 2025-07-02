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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "login", value = "/login")
public class LoginServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateUtil.processTemplate("login", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        System.out.println("🔐 Tentative de connexion pour: " + email); // DEBUG

        Utilisateur user = utilisateurRepository.findByEmail(email);

        if (user != null) {
            System.out.println("👤 Utilisateur trouvé: " + user.getNomComplet()); // DEBUG
            System.out.println("🔑 Mot de passe en base: " + user.getMotDePasse()); // DEBUG
            
            // Vérifier le mot de passe (texte brut ou BCrypt)
            boolean passwordValid = false;
            
            // Si le mot de passe en base commence par $2a$ c'est du BCrypt
            if (user.getMotDePasse().startsWith("$2a$")) {
                passwordValid = PasswordUtils.checkPassword(motDePasse, user.getMotDePasse());
                System.out.println("🔒 Vérification BCrypt: " + passwordValid); // DEBUG
            } else {
                // Sinon comparaison directe (pour les tests)
                passwordValid = motDePasse.equals(user.getMotDePasse());
                System.out.println("🔓 Vérification texte brut: " + passwordValid); // DEBUG
            }
            
            if (passwordValid) {
                HttpSession session = req.getSession();
                session.setAttribute("utilisateur", user); // CORRECTION: "utilisateur" au lieu de "user"
                System.out.println("✅ Connexion réussie pour: " + user.getNomComplet()); // DEBUG
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }
        } else {
            System.out.println("❌ Utilisateur non trouvé pour email: " + email); // DEBUG
        }
        
        System.out.println("❌ Échec de connexion"); // DEBUG
        TemplateUtil.processTemplate("login", req, resp, Map.of("errorMessage", "Email ou mot de passe incorrect"));
    }
}
