package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Demandesorganisateur;
import fr.esgi.robin.colorrun.business.StatutDemande;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.DemandeOrganisateurRepository;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;
import fr.esgi.robin.colorrun.repository.impl.DemandeOrganisateurRepositoryImpl;
import fr.esgi.robin.colorrun.repository.impl.UtilisateurRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/demandes-organisateur")
public class AdminDemandesServlet extends HttpServlet {
    
    private final DemandeOrganisateurRepository demandeRepository = new DemandeOrganisateurRepositoryImpl();
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérifier si l'utilisateur est connecté et admin
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        if (!utilisateurConnecte.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs");
            return;
        }
        
        // Récupérer toutes les demandes
        List<Demandesorganisateur> demandes = new ArrayList<>();
        Map<StatutDemande, Long> stats = new HashMap<>();
        
        try {
            demandes = demandeRepository.findAll();
            
            // Calculer les statistiques
            stats.put(StatutDemande.EN_ATTENTE, demandes.stream().filter(d -> d.getStatut() == StatutDemande.EN_ATTENTE).count());
            stats.put(StatutDemande.ACCEPTE, demandes.stream().filter(d -> d.getStatut() == StatutDemande.ACCEPTE).count());
            stats.put(StatutDemande.REFUSE, demandes.stream().filter(d -> d.getStatut() == StatutDemande.REFUSE).count());
            
        } catch (Exception e) {
            // Initialiser des valeurs par défaut en cas d'erreur
            stats.put(StatutDemande.EN_ATTENTE, 0L);
            stats.put(StatutDemande.ACCEPTE, 0L);
            stats.put(StatutDemande.REFUSE, 0L);
            
            session.setAttribute("errorMessage", "Erreur lors du chargement des demandes. Vérifiez que la table existe.");
        }
        
        // Préparer les données pour le template
        Map<String, Object> data = new HashMap<>();
        data.put("demandes", demandes);
        data.put("stats", stats);
        data.put("pageTitle", "Gestion des Demandes Organisateur - Admin");
        data.put("utilisateurConnecte", utilisateurConnecte);
        
        TemplateUtil.processTemplate("admin-demandes", req, resp, data);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérification de sécurité
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            String action = req.getParameter("action");
            String demandeIdStr = req.getParameter("demandeId");
            
            if (action == null || demandeIdStr == null) {
                session.setAttribute("errorMessage", "Paramètres manquants");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            Integer demandeId = Integer.parseInt(demandeIdStr);
            Demandesorganisateur demande = demandeRepository.findById(demandeId);
            
            if (demande == null) {
                session.setAttribute("errorMessage", "Demande non trouvée");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            switch (action) {
                case "accepter":
                    // Mettre à jour le statut de la demande
                    demande.setStatut(StatutDemande.ACCEPTE);
                    demandeRepository.update(demande);
                    
                    // PROMOUVOIR L'UTILISATEUR EN ORGANISATEUR
                    Utilisateur utilisateur = demande.getUtilisateur();
                    utilisateur.setRoleString("organisateur");
                    utilisateurRepository.update(utilisateur);
                    
                    session.setAttribute("successMessage", 
                        "✅ Demande acceptée ! " + utilisateur.getNomComplet() + " est maintenant organisateur.");
                    break;
                    
                case "refuser":
                    // Mettre à jour seulement le statut de la demande (ne pas changer le rôle)
                    demande.setStatut(StatutDemande.REFUSE);
                    demandeRepository.update(demande);
                    
                    session.setAttribute("successMessage", 
                        "❌ Demande de " + demande.getUtilisateur().getNomComplet() + " refusée.");
                    break;
                    
                case "supprimer":
                    String nomUtilisateur = demande.getUtilisateur().getNomComplet();
                    demandeRepository.delete(demande);
                    
                    session.setAttribute("successMessage", 
                        "🗑️ Demande de " + nomUtilisateur + " supprimée.");
                    break;
                    
                default:
                    session.setAttribute("errorMessage", "Action non reconnue: " + action);
            }
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Erreur lors du traitement de la demande: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getRequestURI());
    }
} 