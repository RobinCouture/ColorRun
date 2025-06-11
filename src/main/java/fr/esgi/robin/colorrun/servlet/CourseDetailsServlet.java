package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.FilsDiscussion;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.FilsDiscussionRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
import fr.esgi.robin.colorrun.repository.impl.FilsDiscussionRepositoryImpl;
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

@WebServlet(name = "courseDetails", value = "/course/*")
public class CourseDetailsServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();
    private final FilsDiscussionRepository filsDiscussionRepository = new FilsDiscussionRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Traitement de l'envoi de message
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("sendMessage".equals(action)) {
            try {
                String pathInfo = req.getPathInfo();
                Integer courseId = Integer.parseInt(pathInfo.substring(1));
                String contenu = req.getParameter("message");
                
                if (contenu != null && !contenu.trim().isEmpty()) {
                    // Récupérer la course
                    Courses course = coursesRepository.findById(courseId);
                    
                    if (course != null) {
                        // Créer le message
                        FilsDiscussion filsDiscussion = new FilsDiscussion();
                        filsDiscussion.setContenu(contenu.trim());
                        filsDiscussion.setDateEnvoi(Instant.now());
                        filsDiscussion.setUtilisateur(utilisateurConnecte);
                        filsDiscussion.setCourse(course);
                        
                        // Sauvegarder le message
                        filsDiscussionRepository.save(filsDiscussion);
                        
                        session.setAttribute("successMessage", "Message envoyé avec succès !");
                    } else {
                        session.setAttribute("errorMessage", "Course non trouvée");
                    }
                } else {
                    session.setAttribute("errorMessage", "Le message ne peut pas être vide");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du message: " + e.getMessage());
                session.setAttribute("errorMessage", "Erreur lors de l'envoi du message");
                e.printStackTrace();
            }
        }
        
        // Redirection pour éviter la resoumission
        resp.sendRedirect(req.getRequestURI());
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("🚀 CourseDetailsServlet - processRequest appelé");
            
            // Extraire l'ID de l'URL (/course/1 -> 1)
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de course manquant");
                return;
            }
            
            String courseIdStr = pathInfo.substring(1);
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
            
            // Récupérer les messages de la course
            List<FilsDiscussion> filsDiscussion = filsDiscussionRepository.findByCourseId(courseId);
            System.out.println("📨 Nombre de messages trouvés: " + filsDiscussion.size());
            
            // Debug: afficher les messages trouvés
            for (FilsDiscussion message : filsDiscussion) {
                System.out.println("  - Message ID: " + message.getIdMessage() + 
                                 ", Contenu: " + message.getContenu() + 
                                 ", Auteur: " + (message.getUtilisateur() != null ? message.getUtilisateur().getNomComplet() : "null"));
            }
            
            // Debug: vérifier la variable passée au template
            System.out.println("🔍 Variable filsDiscussion passée au template: " + filsDiscussion);
            
            // Vérifier si l'utilisateur est connecté pour le chat
            HttpSession session = req.getSession();
            Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
            boolean peutEnvoyerMessage = utilisateurConnecte != null;
            
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("course", course);
            data.put("filsDiscussion", filsDiscussion);
            data.put("peutEnvoyerMessage", peutEnvoyerMessage);
            data.put("pageTitle", course.getNomCourse() + " - ColorRun");
            
            TemplateUtil.processTemplate("course-details", req, resp, data);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur dans CourseDetailsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des détails de la course");
        }
    }
}