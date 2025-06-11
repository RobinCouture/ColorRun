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
import java.util.Map;

@WebServlet(name = "courseDetails", value = "/course/*")
public class CourseDetailsServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("🚀 CourseDetailsServlet - doGet appelé");
            
            // Extraire l'ID de l'URL (/course/1 -> 1)
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de course manquant");
                return;
            }
            
            String courseIdStr = pathInfo.substring(1); // Enlever le slash initial
            Integer courseId;
            
            try {
                courseId = Integer.parseInt(courseIdStr);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de course invalide");
                return;
            }
            
            System.out.println("📍 Recherche de la course avec ID: " + courseId);
            
            // Récupérer la course par ID
            Courses course = coursesRepository.findById(courseId);
            
            if (course == null) {
                System.out.println("❌ Course non trouvée pour ID: " + courseId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }
            
            System.out.println("✅ Course trouvée: " + course.getNomCourse());
            
            // Passer la course au template
            TemplateUtil.processTemplate("course-details", req, resp, Map.of("course", course));
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans CourseDetailsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des détails de la course");
        }
    }
} 