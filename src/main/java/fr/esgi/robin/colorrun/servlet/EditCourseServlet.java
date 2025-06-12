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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "editCourse", value = "/edit-course/*")
public class EditCourseServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Extraire l'ID de l'URL
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de course manquant");
                return;
            }
            
            Integer courseId = Integer.parseInt(pathInfo.substring(1));
            Courses course = coursesRepository.findById(courseId);
            
            if (course == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }
            
            // Vérifier les permissions
            if (!canManageCourse(utilisateurConnecte, course)) {
                session.setAttribute("errorMessage", "Vous n'avez pas l'autorisation de modifier cette course");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                return;
            }
            
            // Formatter la date pour le formulaire
            String dateTimeFormatted = "";
            if (course.getDateHeure() != null) {
                LocalDateTime localDateTime = LocalDateTime.ofInstant(course.getDateHeure(), ZoneId.systemDefault());
                dateTimeFormatted = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("course", course);
            data.put("dateTimeFormatted", dateTimeFormatted);
            data.put("pageTitle", "Modifier " + course.getNomCourse() + " - ColorRun");
            
            TemplateUtil.processTemplate("edit-course", req, resp, data);
            
        } catch (Exception e) {
            System.err.println("Erreur dans EditCourseServlet doGet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement de la page de modification");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
        
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Extraire l'ID de l'URL
            String pathInfo = req.getPathInfo();
            Integer courseId = Integer.parseInt(pathInfo.substring(1));
            
            Courses course = coursesRepository.findById(courseId);
            
            if (course == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }
            
            // Vérifier les permissions
            if (!canManageCourse(utilisateurConnecte, course)) {
                session.setAttribute("errorMessage", "Vous n'avez pas l'autorisation de modifier cette course");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                return;
            }
            
            // Récupérer les données du formulaire
            String nomCourse = req.getParameter("nomCourse");
            String description = req.getParameter("description");
            String dateHeureStr = req.getParameter("dateHeure");
            String lieu = req.getParameter("lieu");
            String distanceStr = req.getParameter("distance");
            String prixStr = req.getParameter("prix");
            String nbMaxParticipantsStr = req.getParameter("nbMaxParticipants");
            String causeSoutenue = req.getParameter("causeSoutenue");
            String imageUrl = req.getParameter("imageUrl");
            
            // Validation basique
            if (nomCourse == null || nomCourse.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                dateHeureStr == null || dateHeureStr.trim().isEmpty() ||
                lieu == null || lieu.trim().isEmpty() ||
                distanceStr == null || distanceStr.trim().isEmpty() ||
                prixStr == null || prixStr.trim().isEmpty() ||
                nbMaxParticipantsStr == null || nbMaxParticipantsStr.trim().isEmpty()) {
                
                session.setAttribute("errorMessage", "Tous les champs obligatoires doivent être remplis");
                resp.sendRedirect(req.getRequestURI());
                return;
            }
            
            try {
                // Conversion des données
                Double distance = Double.parseDouble(distanceStr);
                Double prix = Double.parseDouble(prixStr);
                Integer nbMaxParticipants = Integer.parseInt(nbMaxParticipantsStr);
                
                // Conversion de la date
                LocalDateTime localDateTime = LocalDateTime.parse(dateHeureStr);
                Instant dateHeure = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                
                // Mettre à jour l'objet course
                course.setNomCourse(nomCourse.trim());
                course.setDescription(description.trim());
                course.setDateHeure(dateHeure);
                course.setLieu(lieu.trim());
                course.setDistance(distance);
                course.setPrix(prix);
                course.setNbMaxParticipants(nbMaxParticipants);
                course.setCauseSoutenue(causeSoutenue != null && !causeSoutenue.trim().isEmpty() ? causeSoutenue.trim() : null);
                course.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : null);
                
                // Sauvegarder
                coursesRepository.update(course);
                
                session.setAttribute("successMessage", "Course '" + course.getNomCourse() + "' modifiée avec succès !");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "Erreur dans les données numériques (distance, prix, participants)");
                resp.sendRedirect(req.getRequestURI());
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur dans le format de la date");
                resp.sendRedirect(req.getRequestURI());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur dans EditCourseServlet doPost: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur lors de la modification de la course");
            resp.sendRedirect(req.getRequestURI());
        }
    }
    
    /**
     * Vérifie si un utilisateur peut gérer une course
     */
    private boolean canManageCourse(Utilisateur utilisateur, Courses course) {
        if (utilisateur == null) return false;
        
        // Les admins peuvent tout faire
        if (utilisateur.isAdmin()) return true;
        
        // Les organisateurs peuvent gérer leurs propres courses
        if (course.getUtilisateur() != null && 
            course.getUtilisateur().getId().equals(utilisateur.getId())) {
            return true;
        }
        
        return false;
    }
}