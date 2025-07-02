package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.InscriptionRepository;
import fr.esgi.robin.colorrun.repository.impl.InscriptionRepositoryImpl;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "profil", value = "/profile")
public class ProfilServlet extends HttpServlet {
    
    private final InscriptionRepository inscriptionRepository = new InscriptionRepositoryImpl();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("🚀 ProfilServlet - Accès à la page profil");
        
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        System.out.println("👤 Utilisateur en session: " + (utilisateurConnecte != null ? utilisateurConnecte.getNomComplet() : "AUCUN"));
        
        if (utilisateurConnecte == null) {
            System.out.println("❌ Aucun utilisateur connecté - Redirection vers login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            System.out.println("🔍 Recherche des inscriptions pour l'utilisateur ID: " + utilisateurConnecte.getId());
            System.out.println("👤 Détails utilisateur: " + utilisateurConnecte.getPrenom() + " " + utilisateurConnecte.getNom() + " (" + utilisateurConnecte.getEmail() + ")");
            
            // Récupérer toutes les inscriptions de l'utilisateur
            InscriptionRepositoryImpl impl = (InscriptionRepositoryImpl) inscriptionRepository;
            List<Inscription> inscriptions = impl.findByUserId(utilisateurConnecte.getId());
            
            System.out.println("📋 Inscriptions trouvées pour " + utilisateurConnecte.getNomComplet() + ": " + inscriptions.size());
            
            // Debug détaillé pour chaque inscription
            for (int i = 0; i < inscriptions.size(); i++) {
                Inscription inscription = inscriptions.get(i);
                System.out.println("📝 Inscription " + (i+1) + ":");
                System.out.println("   - ID: " + inscription.getId());
                System.out.println("   - Dossard: " + inscription.getDossard());
                System.out.println("   - Course: " + (inscription.getCourse() != null ? inscription.getCourse().getNomCourse() : "NULL"));
                System.out.println("   - Date course: " + (inscription.getCourse() != null ? inscription.getCourse().getDateHeure() : "NULL"));
                System.out.println("   - Lieu: " + (inscription.getCourse() != null ? inscription.getCourse().getLieu() : "NULL"));
                System.out.println("   - Distance: " + (inscription.getCourse() != null ? inscription.getCourse().getDistance() : "NULL"));
                System.out.println("   - Description: " + (inscription.getCourse() != null ? inscription.getCourse().getDescription() : "NULL"));
            }
            
            // Ajouter l'instant actuel pour les comparaisons dans le template
            Instant maintenant = Instant.now();
            System.out.println("⏰ Instant actuel: " + maintenant);
            
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("utilisateur", utilisateurConnecte);
            data.put("inscriptions", inscriptions);
            data.put("maintenant", maintenant);
            data.put("pageTitle", "Mon Profil - ColorRun");
            
            System.out.println("✅ Rendu du template profile avec " + inscriptions.size() + " inscriptions");
            TemplateUtil.processTemplate("profile", req, resp, data);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur dans ProfilServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement du profil");
        }
    }
} 