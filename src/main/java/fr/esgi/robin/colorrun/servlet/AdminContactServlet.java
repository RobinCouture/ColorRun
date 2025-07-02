package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.DemandeContact;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.DemandeContactRepository;
import fr.esgi.robin.colorrun.repository.impl.DemandeContactRepositoryImpl;
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

@WebServlet("/admin/demandes-contact")
public class AdminContactServlet extends HttpServlet {
    
    private DemandeContactRepository demandeContactRepository = new DemandeContactRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérification des droits admin
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateur == null || !utilisateur.getRoleString().equals("admin")) {
            response.sendRedirect("/login");
            return;
        }
        
        try {
            // Récupération de toutes les demandes
            List<DemandeContact> demandes = demandeContactRepository.findAll();
            
            // Statistiques
            long demandesEnAttente = demandes.stream().filter(d -> !d.isTraite()).count();
            long demandesTraitees = demandes.stream().filter(DemandeContact::isTraite).count();
            
            Map<String, Object> model = new HashMap<>();
            model.put("demandes", demandes);
            model.put("demandesEnAttente", demandesEnAttente);
            model.put("demandesTraitees", demandesTraitees);
            model.put("totalDemandes", demandes.size());
            
            System.out.println("📊 Admin consulte les demandes de contact: " + demandes.size() + " demandes");
            
            TemplateUtil.processTemplate("admin-demandes-contact", request, response, model);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des demandes de contact: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérification des droits admin
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateur == null || !utilisateur.getRoleString().equals("admin")) {
            response.sendRedirect("/login");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            int demandeId = Integer.parseInt(request.getParameter("demandeId"));
            
            if ("marquer_traite".equals(action)) {
                String reponse = request.getParameter("reponse");
                demandeContactRepository.updateStatut(demandeId, true, reponse);
                System.out.println("✅ Demande " + demandeId + " marquée comme traitée");
            } else if ("marquer_non_traite".equals(action)) {
                demandeContactRepository.updateStatut(demandeId, false, null);
                System.out.println("🔄 Demande " + demandeId + " marquée comme non traitée");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/demandes-contact");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour de la demande: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
} 