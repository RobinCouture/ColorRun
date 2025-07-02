package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "editCourse", value = "/edit-course/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class EditCourseServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();
    private static final String UPLOAD_DIRECTORY = "course-images";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

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
            
            // Récupérer l'URL de l'image actuelle
            String currentImageUrl = coursesRepository.getImagePathById(courseId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("course", course);
            data.put("dateTimeFormatted", dateTimeFormatted);
            data.put("currentImageUrl", currentImageUrl);
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
            String currentImageUrl = coursesRepository.getImagePathById(courseId);
            
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
                
                // Traitement de l'image
                String newImageUrl = null;
                String newImageName = null;
                Part imagePart = req.getPart("courseImage");
                
                if (imagePart != null && imagePart.getSize() > 0) {
                    // Une nouvelle image a été uploadée
                    newImageUrl = handleImageUpload(imagePart, req);
                    newImageName = imagePart.getSubmittedFileName();
                    if (newImageUrl == null) {
                        session.setAttribute("errorMessage", "Erreur lors de l'upload de l'image. Vérifiez le format (JPG, PNG, GIF, WEBP) et la taille (max 5MB).");
                        resp.sendRedirect(req.getRequestURI());
                        return;
                    }
                }
                
                // Mettre à jour l'objet course
                course.setNomCourse(nomCourse.trim());
                course.setDescription(description.trim());
                course.setDateHeure(dateHeure);
                course.setLieu(lieu.trim());
                course.setDistance(distance);
                course.setPrix(prix);
                course.setNbMaxParticipants(nbMaxParticipants);
                course.setCauseSoutenue(causeSoutenue != null && !causeSoutenue.trim().isEmpty() ? causeSoutenue.trim() : null);
                course.setImageUrl(newImageUrl);
                
                // Sauvegarder
                coursesRepository.update(course);
                
                // Mettre à jour l'image en base si nécessaire
                if (newImageUrl != null && imagePart.getSize() > 0 && currentImageUrl != null) {
                    coursesRepository.updateImageCourse(courseId, newImageUrl, newImageName);
                    System.out.println("Image de course mise à jour: " + newImageUrl);
                } else if (currentImageUrl == null) {
                    coursesRepository.uploadImageCourse(courseId, newImageUrl, newImageName);
                }
                
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
     * Gère l'upload d'image (copié depuis CreateCourseServlet)
     */
    private String handleImageUpload(Part imagePart, HttpServletRequest request) {
        try {
            String fileName = imagePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                return null;
            }
            
            // Vérifier l'extension
            String fileExtension = getFileExtension(fileName).toLowerCase();
            if (!isAllowedExtension(fileExtension)) {
                return null;
            }
            
            // Créer le répertoire de destination s'il n'existe pas
            String realPath = getServletContext().getRealPath("/");
            Path uploadPath = Paths.get(realPath, "ressources", "images", UPLOAD_DIRECTORY);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom unique pour le fichier
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = uploadPath.resolve(uniqueFileName);
            
            // Sauvegarder le fichier
            try (InputStream inputStream = imagePart.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Construire l'URL pour accéder à l'image
            return request.getContextPath() + "/ressources/images/" + UPLOAD_DIRECTORY + "/" + uniqueFileName;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'upload d'image: " + e.getMessage());
            return null;
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vérifie si un utilisateur peut gérer une course
     */
    private boolean canManageCourse(Utilisateur utilisateur, Courses course) {
        if (utilisateur == null) return false;
        
        // Les admins peuvent tout faire
        if (utilisateur.isAdmin()) return true;

        if (utilisateur.isOrganisateur()) return true;
        
        // Les organisateurs peuvent gérer leurs propres courses
        if (course.getUtilisateur() != null && 
            course.getUtilisateur().getId().equals(utilisateur.getId())) {
            return true;
        }
        
        return false;
    }
}