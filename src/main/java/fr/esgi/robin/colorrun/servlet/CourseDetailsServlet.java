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
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("sendMessage".equals(action)) {
            handleSendMessage(req, resp, utilisateurConnecte);
            // Redirection pour éviter la resoumission
            resp.sendRedirect(req.getRequestURI());
        } else if ("deleteCourse".equals(action)) {
            handleDeleteCourse(req, resp, utilisateurConnecte);
            // PAS de redirection supplémentaire - handleDeleteCourse le fait déjà
            return;
        }
    }

    private void handleSendMessage(HttpServletRequest req, HttpServletResponse resp, Utilisateur utilisateurConnecte) {
        HttpSession session = req.getSession();
        try {
            String pathInfo = req.getPathInfo();
            Integer courseId = Integer.parseInt(pathInfo.substring(1));
            String contenu = req.getParameter("message");

            if (contenu != null && !contenu.trim().isEmpty()) {
                Courses course = coursesRepository.findById(courseId);

                if (course != null) {
                    FilsDiscussion filsDiscussion = new FilsDiscussion();
                    filsDiscussion.setContenu(contenu.trim());
                    filsDiscussion.setDateEnvoi(Instant.now());
                    filsDiscussion.setUtilisateur(utilisateurConnecte);
                    filsDiscussion.setCourse(course);

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

    private void handleDeleteCourse(HttpServletRequest req, HttpServletResponse resp, Utilisateur utilisateurConnecte) throws IOException {
        HttpSession session = req.getSession();
        try {
            String pathInfo = req.getPathInfo();
            Integer courseId = Integer.parseInt(pathInfo.substring(1));

            Courses course = coursesRepository.findById(courseId);

            if (course == null) {
                session.setAttribute("errorMessage", "Course non trouvée");
                return;
            }

            // Vérifier les permissions
            if (!canManageCourse(utilisateurConnecte, course)) {
                session.setAttribute("errorMessage", "Vous n'avez pas l'autorisation de supprimer cette course");
                return;
            }

            // Supprimer d'abord tous les messages associés
            List<FilsDiscussion> messages = filsDiscussionRepository.findByCourseId(courseId);
            for (FilsDiscussion message : messages) {
                filsDiscussionRepository.deleteById(message.getId());
            }

            // Supprimer la course
            coursesRepository.delete(course);

            session.setAttribute("successMessage", "Course '" + course.getNomCourse() + "' supprimée avec succès");

            // Rediriger vers la page des courses
            resp.sendRedirect(req.getContextPath() + "/courses");
            return;

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de la course: " + e.getMessage());
            session.setAttribute("errorMessage", "Erreur lors de la suppression de la course");
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si un utilisateur peut gérer une course (modifier/supprimer)
     * Version améliorée avec logs de débogage
     */
    private boolean canManageCourse(Utilisateur utilisateur, Courses course) {
        if (utilisateur == null) {
            System.out.println("🚫 canManageCourse: utilisateur null");
            return false;
        }

        System.out.println("🔍 canManageCourse: Vérification pour utilisateur " + utilisateur.getNomComplet());
        System.out.println("🔍 canManageCourse: Email = " + utilisateur.getEmail());
        System.out.println("🔍 canManageCourse: Rôle utilisateur = '" + utilisateur.getRoleString() + "'");
        System.out.println("🔍 canManageCourse: isAdmin() = " + utilisateur.isAdmin());
        System.out.println("🔍 canManageCourse: isOrganisateur() = " + utilisateur.isOrganisateur());

        // Les admins peuvent tout faire
        if (utilisateur.isAdmin()) {
            System.out.println("✅ canManageCourse: Autorisé (admin)");
            return true;
        }

        // Les organisateurs peuvent gérer TOUTES les courses
        if (utilisateur.isOrganisateur()) {
            System.out.println("✅ canManageCourse: Autorisé (organisateur via méthode)");
            return true;
        }

        // Alternative : vérification directe sur la chaîne de caractères (case insensitive)
        if ("organisateur".equalsIgnoreCase(utilisateur.getRoleString())) {
            System.out.println("✅ canManageCourse: Autorisé (organisateur par string)");
            return true;
        }

        // Les utilisateurs normaux peuvent gérer seulement leurs propres courses
        if (course.getUtilisateur() != null &&
                course.getUtilisateur().getId().equals(utilisateur.getId())) {
            System.out.println("✅ canManageCourse: Autorisé (propriétaire de la course)");
            return true;
        }

        System.out.println("🚫 canManageCourse: Accès refusé - Rôle: '" + utilisateur.getRoleString() + "'");
        return false;
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

            String pathImage = coursesRepository.getImagePathById(courseId);
    
            // Récupérer les messages de la course
            List<FilsDiscussion> filsDiscussion = filsDiscussionRepository.findByCourseId(courseId);
            System.out.println("📨 Nombre de messages trouvés: " + filsDiscussion.size());
    
            // Vérifier si l'utilisateur est connecté
            HttpSession session = req.getSession();
            Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");
            boolean peutEnvoyerMessage = utilisateurConnecte != null;
    
            // Vérifier si l'utilisateur peut gérer cette course (pour afficher boutons modifier/supprimer)
            boolean peutGererCourse = canManageCourse(utilisateurConnecte, course);
            
            // Vérifier si l'utilisateur peut s'inscrire (tous les utilisateurs connectés peuvent s'inscrire)
            boolean peutSinscrire = utilisateurConnecte != null;
    
            // Vérifier si les inscriptions sont ouvertes
            boolean inscriptionsOuvertes = course.getDateHeure().isAfter(Instant.now());

            // Vérifier si l'utilisateur est déjà inscrit
            boolean dejaInscrit = false;
    
            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("course", course);
            data.put("filsDiscussion", filsDiscussion);
            data.put("peutEnvoyerMessage", peutEnvoyerMessage);
            data.put("peutGererCourse", peutGererCourse);
            data.put("peutSinscrire", peutSinscrire);
            data.put("inscriptionsOuvertes", inscriptionsOuvertes);
            data.put("dejaInscrit", dejaInscrit);
            data.put("pageTitle", course.getNomCourse() + " - ColorRun");
            data.put("pathImage", pathImage);
    
            TemplateUtil.processTemplate("course-details", req, resp, data);
    
        } catch (Exception e) {
            System.out.println("❌ Erreur dans CourseDetailsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des détails de la course");
        }
    }
}