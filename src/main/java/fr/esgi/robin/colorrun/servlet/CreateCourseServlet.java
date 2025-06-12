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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/create-course")
public class CreateCourseServlet extends HttpServlet {
    
    private CoursesRepository coursesRepository = new CoursesRepositoryImpl();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=== CreateCourseServlet GET ===");
        
        HttpSession session = req.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérifier si l'utilisateur est connecté
        if (utilisateur == null) {
            System.out.println("Utilisateur non connecté, redirection vers login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        System.out.println("Utilisateur connecté: " + utilisateur.getEmail() + " - Rôle: " + utilisateur.getRoleString());
        
        // Vérifier si l'utilisateur a les droits (admin ou organisateur)
        if (!utilisateur.isAdmin() && !"organisateur".equals(utilisateur.getRoleString())) {
            System.out.println("Accès refusé - Utilisateur sans droits de création");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès interdit : vous devez être admin ou organisateur pour créer une course");
            return;
        }
        
        // Préparer les variables pour le template
        Map<String, Object> variables = new HashMap<>();
        variables.put("pageTitle", "Créer une course");
        
        // Afficher le formulaire
        TemplateUtil.processTemplate("create-course", req, resp, variables);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=== CreateCourseServlet POST ===");
        
        HttpSession session = req.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        
        // Vérification de sécurité
        if (utilisateur == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        if (!utilisateur.isAdmin() && !"organisateur".equals(utilisateur.getRoleString())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            // Récupérer les données du formulaire
            String nomCourse = req.getParameter("nomCourse");
            String description = req.getParameter("description");
            String dateHeureStr = req.getParameter("dateHeure");
            String lieu = req.getParameter("lieu");
            String distanceStr = req.getParameter("distance");
            String prixStr = req.getParameter("prix");
            String nbMaxParticipantsStr = req.getParameter("nbMaxParticipants");
            String causeSoutenue = req.getParameter("causeSoutenue");
            
            System.out.println("Données reçues:");
            System.out.println("- Nom: " + nomCourse);
            System.out.println("- Description: " + description);
            System.out.println("- Date/Heure: " + dateHeureStr);
            System.out.println("- Lieu: " + lieu);
            System.out.println("- Distance: " + distanceStr);
            System.out.println("- Prix: " + prixStr);
            System.out.println("- Nb participants: " + nbMaxParticipantsStr);
            System.out.println("- Cause: " + causeSoutenue);
            
            // Validation des champs obligatoires
            StringBuilder errors = new StringBuilder();
            
            if (nomCourse == null || nomCourse.trim().isEmpty()) {
                errors.append("Le nom de la course est obligatoire. ");
            }
            
            if (dateHeureStr == null || dateHeureStr.trim().isEmpty()) {
                errors.append("La date et l'heure sont obligatoires. ");
            }
            
            if (lieu == null || lieu.trim().isEmpty()) {
                errors.append("Le lieu est obligatoire. ");
            }
            
            if (distanceStr == null || distanceStr.trim().isEmpty()) {
                errors.append("La distance est obligatoire. ");
            }
            
            if (prixStr == null || prixStr.trim().isEmpty()) {
                errors.append("Le prix est obligatoire. ");
            }
            
            if (nbMaxParticipantsStr == null || nbMaxParticipantsStr.trim().isEmpty()) {
                errors.append("Le nombre maximum de participants est obligatoire. ");
            }
            
            // S'il y a des erreurs de validation, retourner au formulaire
            if (errors.length() > 0) {
                System.out.println("Erreurs de validation: " + errors.toString());
                Map<String, Object> variables = new HashMap<>();
                variables.put("pageTitle", "Créer une course");
                variables.put("error", errors.toString());
                addFormDataToVariables(variables, nomCourse, description, dateHeureStr, lieu, distanceStr, prixStr, nbMaxParticipantsStr, causeSoutenue);
                
                TemplateUtil.processTemplate("create-course", req, resp, variables);
                return;
            }
            
            // Conversion et validation des types numériques
            Instant dateHeure = null;
            Double distance = null;
            Double prix = null;
            Integer nbMaxParticipants = null;
            
            try {
                // Conversion de la date
                LocalDateTime localDateTime = LocalDateTime.parse(dateHeureStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                dateHeure = localDateTime.toInstant(ZoneOffset.UTC);
                System.out.println("Date convertie: " + dateHeure);
                
                // Conversion des nombres
                distance = Double.parseDouble(distanceStr);
                if (distance <= 0) {
                    throw new NumberFormatException("La distance doit être positive");
                }
                
                prix = Double.parseDouble(prixStr);
                if (prix < 0) {
                    throw new NumberFormatException("Le prix ne peut pas être négatif");
                }
                
                nbMaxParticipants = Integer.parseInt(nbMaxParticipantsStr);
                if (nbMaxParticipants <= 0) {
                    throw new NumberFormatException("Le nombre de participants doit être positif");
                }
                
                System.out.println("Conversions réussies:");
                System.out.println("- Distance: " + distance + " km");
                System.out.println("- Prix: " + prix + " €");
                System.out.println("- Participants max: " + nbMaxParticipants);
                
            } catch (DateTimeParseException e) {
                System.out.println("Erreur de format de date: " + e.getMessage());
                Map<String, Object> variables = new HashMap<>();
                variables.put("pageTitle", "Créer une course");
                variables.put("error", "Format de date invalide. Utilisez le format JJ/MM/AAAA HH:MM");
                addFormDataToVariables(variables, nomCourse, description, dateHeureStr, lieu, distanceStr, prixStr, nbMaxParticipantsStr, causeSoutenue);
                
                TemplateUtil.processTemplate("create-course", req, resp, variables);
                return;
                
            } catch (NumberFormatException e) {
                System.out.println("Erreur de format numérique: " + e.getMessage());
                Map<String, Object> variables = new HashMap<>();
                variables.put("pageTitle", "Créer une course");
                variables.put("error", "Erreur dans les valeurs numériques: " + e.getMessage());
                addFormDataToVariables(variables, nomCourse, description, dateHeureStr, lieu, distanceStr, prixStr, nbMaxParticipantsStr, causeSoutenue);
                
                TemplateUtil.processTemplate("create-course", req, resp, variables);
                return;
            }
            
            // Créer la course
            Courses course = new Courses();
            course.setNomCourse(nomCourse.trim());
            course.setDescription(description != null ? description.trim() : "");
            course.setDateHeure(dateHeure);
            course.setLieu(lieu.trim());
            course.setDistance(distance);
            course.setPrix(prix);
            course.setNbMaxParticipants(nbMaxParticipants);
            course.setCauseSoutenue(causeSoutenue != null ? causeSoutenue.trim() : "");
            course.setUtilisateur(utilisateur);
            
            System.out.println("Création de la course avec organisateur ID: " + utilisateur.getId());
            
            // Sauvegarder en base de données
            coursesRepository.create(course);
            System.out.println("Course créée avec succès !");
            
            // Rediriger vers la liste des courses avec message de succès
            resp.sendRedirect(req.getContextPath() + "/courses?success=" + 
                java.net.URLEncoder.encode("Course '" + nomCourse + "' créée avec succès !", "UTF-8"));
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la course:");
            e.printStackTrace();
            
            // En cas d'erreur, retourner au formulaire avec le message d'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("pageTitle", "Créer une course");
            variables.put("error", "Erreur lors de la création de la course : " + e.getMessage());
            
            // Récupérer les données du formulaire pour les remettre
            addFormDataToVariables(variables, 
                req.getParameter("nomCourse"),
                req.getParameter("description"),
                req.getParameter("dateHeure"),
                req.getParameter("lieu"),
                req.getParameter("distance"),
                req.getParameter("prix"),
                req.getParameter("nbMaxParticipants"),
                req.getParameter("causeSoutenue"));
            
            TemplateUtil.processTemplate("create-course", req, resp, variables);
        }
    }
    
    /**
     * Méthode helper pour ajouter les données du formulaire aux variables du template
     */
    private void addFormDataToVariables(Map<String, Object> variables, String nomCourse, String description, 
                                      String dateHeure, String lieu, String distance, String prix, 
                                      String nbMaxParticipants, String causeSoutenue) {
        variables.put("nomCourse", nomCourse);
        variables.put("description", description);
        variables.put("dateHeure", dateHeure);
        variables.put("lieu", lieu);
        variables.put("distance", distance);
        variables.put("prix", prix);
        variables.put("nbMaxParticipants", nbMaxParticipants);
        variables.put("causeSoutenue", causeSoutenue);
    }
}