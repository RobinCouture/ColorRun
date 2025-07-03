package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Role;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminRoles", value = "/admin/roles")
public class AdminRoleServlet extends HttpServlet {
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();
    private static final int USERS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier si l'utilisateur est connecté et admin
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            System.out.println("🔍 Début du chargement de la page admin rôles");
            
            // Récupérer le numéro de page depuis les paramètres (défaut: 1)
            int page = 1;
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            // Calculer l'offset
            int offset = (page - 1) * USERS_PER_PAGE;
            System.out.println("📄 Page: " + page + ", Offset: " + offset + ", Limit: " + USERS_PER_PAGE);

            // Récupérer le nombre total d'utilisateurs et les utilisateurs de la page
            int totalUsers = utilisateurRepository.countAll();
            System.out.println("👥 Nombre total d'utilisateurs: " + totalUsers);
            
            List<Utilisateur> utilisateurs = utilisateurRepository.findAllPaginated(offset, USERS_PER_PAGE);
            System.out.println("📋 Utilisateurs récupérés: " + utilisateurs.size());

            // Calculer les informations de pagination
            int totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / USERS_PER_PAGE));
            System.out.println("📚 Pages totales: " + totalPages);
            
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("utilisateurs", utilisateurs);
            data.put("currentPage", page);
            data.put("totalPages", totalPages);
            data.put("totalUsers", totalUsers);
            data.put("usersPerPage", USERS_PER_PAGE);
            data.put("hasPrevious", page > 1);
            data.put("hasNext", page < totalPages);
            data.put("pageTitle", "Gestion des Rôles - ColorRun");
            data.put("showPagination", true);

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

            System.out.println("✅ Données préparées, rendu du template");
            TemplateUtil.processTemplate("admin-roles", req, resp, data);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la page admin: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("errorMessage", "Erreur lors du chargement: " + e.getMessage());
            errorData.put("utilisateurs", new ArrayList<>());
            errorData.put("currentPage", 1);
            errorData.put("totalPages", 1);
            errorData.put("totalUsers", 0);
            errorData.put("usersPerPage", USERS_PER_PAGE);
            errorData.put("hasPrevious", false);
            errorData.put("hasNext", false);
            errorData.put("showPagination", false);
            
            try {
                TemplateUtil.processTemplate("admin-roles", req, resp, errorData);
            } catch (Exception templateError) {
                resp.getWriter().write("Erreur critique: " + templateError.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        System.out.println("🔧 POST reçu sur /admin/roles");

        // Vérifier si l'utilisateur est connecté et admin
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            System.out.println("❌ Utilisateur non autorisé");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        System.out.println("🎯 Action demandée: " + action);

        if ("updateRole".equals(action)) {
            try {
                String userIdStr = req.getParameter("userId");
                String newRole = req.getParameter("role");
                String currentPageStr = req.getParameter("currentPage");
                
                System.out.println("📋 Paramètres reçus:");
                System.out.println("   - userId: " + userIdStr);
                System.out.println("   - newRole: " + newRole);
                System.out.println("   - currentPage: " + currentPageStr);

                if (userIdStr != null && newRole != null) {
                    Integer userId = Integer.parseInt(userIdStr);
                    Utilisateur utilisateur = utilisateurRepository.findById(userId);
                    
                    if (utilisateur != null) {
                        System.out.println("👤 Utilisateur trouvé: " + utilisateur.getEmail());
                        
                        // Vérifier que le rôle est valide
                        Role role = Role.fromString(newRole);
                        if (role != null) {
                            String oldRole = utilisateur.getRoleString();
                            utilisateur.setRole(role);
                            utilisateurRepository.update(utilisateur);
                            
                            System.out.println("✅ Rôle mis à jour: " + oldRole + " → " + newRole);
                            session.setAttribute("successMessage", 
                                "Rôle de " + utilisateur.getNomComplet() + " mis à jour avec succès ! (" + 
                                oldRole + " → " + newRole + ")");
                        } else {
                            System.out.println("❌ Rôle invalide: " + newRole);
                            session.setAttribute("errorMessage", "Rôle invalide: " + newRole);
                        }
                    } else {
                        System.out.println("❌ Utilisateur non trouvé: " + userId);
                        session.setAttribute("errorMessage", "Utilisateur non trouvé");
                    }
                } else {
                    System.out.println("❌ Paramètres manquants");
                    session.setAttribute("errorMessage", "Paramètres manquants");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la mise à jour du rôle: " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("errorMessage", "Erreur lors de la mise à jour du rôle: " + e.getMessage());
            }
        } else if ("deleteUser".equals(action)) {
            int userId = Integer.parseInt(req.getParameter("userId"));
            utilisateurRepository.deleteById(userId);
            req.getSession().setAttribute("successMessage", "Utilisateur supprimé avec succès !");
            resp.sendRedirect(req.getContextPath() + "/admin/roles");
            return;
        } else {
            System.out.println("❌ Action inconnue: " + action);
            session.setAttribute("errorMessage", "Action non reconnue");
        }

        // Rediriger vers la même page en conservant le numéro de page
        String currentPage = req.getParameter("currentPage");
        String redirectUrl = req.getContextPath() + "/admin/roles";
        
        if (currentPage != null && !currentPage.isEmpty()) {
            try {
                int page = Integer.parseInt(currentPage);
                redirectUrl += "?page=" + page;
            } catch (NumberFormatException e) {
                // Ignorer l'erreur et rediriger vers la page 1
            }
        }
        
        System.out.println("🔄 Redirection vers: " + redirectUrl);
        resp.sendRedirect(redirectUrl);
    }
}