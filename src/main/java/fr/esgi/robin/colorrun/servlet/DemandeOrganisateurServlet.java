package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Demandesorganisateur;
import fr.esgi.robin.colorrun.business.StatutDemande;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.DemandeOrganisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.DemandeOrganisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/demande-organisateur")
public class DemandeOrganisateurServlet extends HttpServlet {
    
    private final DemandeOrganisateurRepository demandeRepository = new DemandeOrganisateurRepositoryImpl();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérifier si l'utilisateur est connecté
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Vérifier si l'utilisateur est déjà organisateur ou admin
        if (utilisateurConnecte.isAdmin() || utilisateurConnecte.isOrganisateur()) {
            session.setAttribute("errorMessage", "Vous êtes déjà organisateur ou administrateur !");
            resp.sendRedirect(req.getContextPath() + "/courses");
            return;
        }
        
        // Préparer les données pour le template
        Map<String, Object> data = new HashMap<>();
        data.put("pageTitle", "Devenir Organisateur - ColorRun");
        data.put("utilisateurConnecte", utilisateurConnecte);
        
        TemplateUtil.processTemplate("demande-organisateur", req, resp, data);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérification de sécurité
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Vérifier si l'utilisateur est déjà organisateur ou admin
        if (utilisateurConnecte.isAdmin() || utilisateurConnecte.isOrganisateur()) {
            session.setAttribute("errorMessage", "Vous êtes déjà organisateur ou administrateur !");
            resp.sendRedirect(req.getContextPath() + "/courses");
            return;
        }
        
        try {
            // Récupérer les données du formulaire
            String motivation = req.getParameter("motivation");
            
            // Validation
            if (motivation == null || motivation.trim().isEmpty()) {
                session.setAttribute("errorMessage", "La motivation est obligatoire pour devenir organisateur");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            if (motivation.trim().length() < 50) {
                session.setAttribute("errorMessage", "La motivation doit contenir au moins 50 caractères");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            // Vérifier si l'utilisateur a déjà une demande en attente
            DemandeOrganisateurRepositoryImpl repoImpl = (DemandeOrganisateurRepositoryImpl) demandeRepository;
            if (repoImpl.existsByUtilisateurAndStatut(utilisateurConnecte.getId(), StatutDemande.EN_ATTENTE)) {
                session.setAttribute("errorMessage", "Vous avez déjà une demande en cours de traitement");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            // Créer la demande
            Demandesorganisateur demande = new Demandesorganisateur();
            demande.setUtilisateur(utilisateurConnecte);
            demande.setMotivation(motivation.trim());
            demande.setStatut(StatutDemande.EN_ATTENTE);
            demande.setDateDemande(Instant.now());
            
            // Sauvegarder
            demandeRepository.create(demande);
            
            session.setAttribute("successMessage", 
                "Votre demande pour devenir organisateur a été envoyée avec succès ! " +
                "Les administrateurs examineront votre candidature prochainement.");
            
            resp.sendRedirect(req.getContextPath() + "/courses");
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Erreur lors de l'envoi de votre demande. Veuillez réessayer.");
            resp.sendRedirect(req.getRequestURI());
        }
    }
} 