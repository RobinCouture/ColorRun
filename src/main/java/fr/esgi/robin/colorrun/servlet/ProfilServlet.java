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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "profil", value = "/profil")
public class ProfilServlet extends HttpServlet {
    
    private final InscriptionRepository inscriptionRepository = new InscriptionRepositoryImpl();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Récupérer toutes les inscriptions de l'utilisateur
            InscriptionRepositoryImpl impl = (InscriptionRepositoryImpl) inscriptionRepository;
            List<Inscription> inscriptions = impl.findByUserId(utilisateurConnecte.getId());
            
            System.out.println("📋 Inscriptions trouvées pour " + utilisateurConnecte.getNomComplet() + ": " + inscriptions.size());
            
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("utilisateur", utilisateurConnecte);
            data.put("inscriptions", inscriptions);
            data.put("pageTitle", "Mon Profil - ColorRun");
            
            TemplateUtil.processTemplate("profil", req, resp, data);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur dans ProfilServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement du profil");
        }
    }
} 