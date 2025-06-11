package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "profile", value = "/profile")
public class ProfileServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier si l'utilisateur est connecté
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Récupérer les données les plus récentes de l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurConnecte.getId());
        
        if (utilisateur == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Préparer les données pour le template
        Map<String, Object> data = new HashMap<>();
        data.put("utilisateur", utilisateur);
        data.put("pageTitle", "Mon Profil - ColorRun");

        // Messages de feedback
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (successMessage != null) {
            data.put("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }
        if (errorMessage != null) {
            data.put("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        TemplateUtil.processTemplate("profile", req, resp, data);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier si l'utilisateur est connecté
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Récupérer les données du formulaire
            String nom = req.getParameter("nom");
            String prenom = req.getParameter("prenom");
            String email = req.getParameter("email");
            String motDePasse = req.getParameter("motDePasse");

            // Valider les données
            if (nom == null || nom.trim().isEmpty() ||
                prenom == null || prenom.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Tous les champs obligatoires doivent être remplis");
                resp.sendRedirect(req.getRequestURI());
                return;
            }

            // Mettre à jour l'utilisateur
            utilisateurConnecte.setNom(nom.trim());
            utilisateurConnecte.setPrenom(prenom.trim());
            utilisateurConnecte.setEmail(email.trim());

            // Si un nouveau mot de passe est fourni
            if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                utilisateurConnecte.setMotDePasse(motDePasse.trim());
            }

            // Sauvegarder les modifications
            utilisateurRepository.update(utilisateurConnecte);

            // Mettre à jour la session
            session.setAttribute("utilisateur", utilisateurConnecte);
            session.setAttribute("successMessage", "Profil mis à jour avec succès !");

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du profil: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur lors de la mise à jour du profil");
        }

        resp.sendRedirect(req.getRequestURI());
    }
} 