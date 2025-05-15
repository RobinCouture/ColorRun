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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
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

        Utilisateur userExist = userRepository.findByEmail(email);
        if (userExist != null) {
            TemplateUtil.processTemplate("register", req, resp, Map.of("errorMessage", "Cet email est déjà utilisé"));
        }

        String hashedPassword = PasswordUtils.hashPassword(motDePasse);

        Utilisateur newUser = new Utilisateur(null, nom, prenom, email, hashedPassword, null, Instant.now());
        userRepository.create(newUser);

        TemplateUtil.processTemplate("login", req, resp);
    }
}
