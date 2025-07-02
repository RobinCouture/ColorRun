package fr.esgi.robin.colorrun;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "helloServlet", urlPatterns = { "", "/" })
public class HelloServlet extends HttpServlet {

    private CoursesRepository coursesRepository;
    private static final int MAX_COURSES_HOME = 3; // Limite pour la page d'accueil

    @Override
    public void init() throws ServletException {
        super.init();
        this.coursesRepository = new CoursesRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("🚀 HelloServlet démarré"); // DEBUG
            
            // Récupérer seulement les 3 premières courses pour la page d'accueil
            List<Courses> upcomingCourses = coursesRepository.findAllPaginated(0, MAX_COURSES_HOME);
            if (upcomingCourses == null) {
                upcomingCourses = new ArrayList<>();
            }

            System.out.println("📈 Nombre de courses récupérées pour la page d'accueil: " + upcomingCourses.size()); // DEBUG
            
            // Affichage des courses pour debug
            for (Courses course : upcomingCourses) {
                System.out.println("🏃 Course: " + course.getNomCourse() + " - " + course.getLieu());
            }

            Map<String, Object> model = new HashMap<>();
            model.put("upcomingCourses", upcomingCourses);

            System.out.println("🎨 Rendu du template home avec " + upcomingCourses.size() + " courses"); // DEBUG
            
            TemplateUtil.processTemplate("home", request, response, model);
        } catch (Exception e) {
            System.out.println("❌ Erreur dans HelloServlet: " + e.getMessage()); // DEBUG
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Une erreur est survenue : " + e.getMessage());
        }
    }

    public void destroy() {
    }
}