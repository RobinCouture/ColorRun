package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
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

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("🚀 CoursesServlet - doGet appelé");
            
            // Récupérer l'utilisateur connecté
            HttpSession session = req.getSession();
            Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
            
            // Récupérer TOUTES les courses disponibles
            List<Courses> allCourses = coursesRepository.findAll();
            System.out.println("📈 Nombre total de courses récupérées: " + (allCourses != null ? allCourses.size() : "null"));
            
            if (allCourses != null) {
                for (Courses course : allCourses) {
                    System.out.println("🏃 Course trouvée: " + course.getNomCourse());
                }
            }
            
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("courses", allCourses != null ? allCourses : List.of());
            data.put("utilisateurConnecte", utilisateurConnecte);
            
            // Passer les données au template
            TemplateUtil.processTemplate("courses", req, resp, data);
            
            System.out.println("✅ Template courses.html rendu avec succès avec toutes les courses");
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans CoursesServlet: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher la page avec une liste vide
            try {
                Map<String, Object> fallbackData = new HashMap<>();
                fallbackData.put("courses", List.of());
                fallbackData.put("utilisateurConnecte", null);
                TemplateUtil.processTemplate("courses", req, resp, fallbackData);
            } catch (Exception e2) {
                System.out.println("❌ Erreur lors du rendu du template de fallback: " + e2.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement de la page courses");
            }
        }
    }
    
    /**
     * Vérifie si un utilisateur peut gérer une course (modifier/supprimer)
     * CORRIGÉ : Les admins ET organisateurs peuvent gérer TOUTES les courses
     */
    public static boolean canManageCourse(Utilisateur utilisateur, Courses course) {
        if (utilisateur == null) return false;
        
        // Les admins peuvent tout faire
        if (utilisateur.isAdmin()) return true;
        
        // Les organisateurs peuvent gérer TOUTES les courses (pas seulement les leurs)
        if ("organisateur".equals(utilisateur.getRoleString())) return true;
        
        // Les utilisateurs normaux peuvent gérer seulement leurs propres courses
        if (course.getUtilisateur() != null && 
            course.getUtilisateur().getId().equals(utilisateur.getId())) {
            return true;
        }
        
        return false;
    }
}