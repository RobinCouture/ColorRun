package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("🚀 CoursesServlet - doGet appelé");
            System.out.println("📍 Request URI: " + req.getRequestURI());
            System.out.println("📍 Context Path: " + req.getContextPath());
            
            // Récupérer toutes les courses
            List<Courses> allCourses = coursesRepository.findAll();
            System.out.println("📈 Nombre de courses récupérées: " + (allCourses != null ? allCourses.size() : "null"));
            
            if (allCourses != null) {
                for (Courses course : allCourses) {
                    System.out.println("🏃 Course trouvée: " + course.getNomCourse());
                }
            }
            
            // Passer les courses au template
            TemplateUtil.processTemplate("courses", req, resp, Map.of("courses", allCourses != null ? allCourses : List.of()));
            
            System.out.println("✅ Template courses.html rendu avec succès");
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans CoursesServlet: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, afficher la page avec une liste vide
            try {
                TemplateUtil.processTemplate("courses", req, resp, Map.of("courses", List.of()));
            } catch (Exception e2) {
                System.out.println("❌ Erreur lors du rendu du template de fallback: " + e2.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement de la page courses");
            }
        }
    }
}