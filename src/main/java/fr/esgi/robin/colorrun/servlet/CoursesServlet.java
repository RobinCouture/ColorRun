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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "courses", value = "/courses")
public class CoursesServlet extends HttpServlet {
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        try {
            // Récupérer tous les courses
            List<Courses> allCourses = coursesRepository.findAll();
            System.out.println("📋 Nombre total de courses trouvées: " + allCourses.size());

            // Filtrer les courses (enlever celles passées et appliquer les filtres)
            List<Courses> filteredCourses = applyFilters(allCourses, req);
            System.out.println("🔍 Courses après filtrage: " + filteredCourses.size());

            Map<Integer, String> courseImages = new HashMap<>();
            for (Courses course : filteredCourses) {
                String imagePath = coursesRepository.getImagePathById(course.getId());
                if (imagePath != null && !imagePath.isEmpty()) {
                    courseImages.put(course.getId(), imagePath);
                }
            }

            // Appliquer le tri
            String sortBy = req.getParameter("sortBy");
            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "date"; // tri par défaut
            }
            filteredCourses = applySorting(filteredCourses, sortBy);

            // Pagination
            int page = 1;
            int pageSize = 6; // 6 courses par page
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            int totalCourses = filteredCourses.size();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCourses);

            List<Courses> paginatedCourses = new ArrayList<>();
            if (startIndex < totalCourses) {
                paginatedCourses = filteredCourses.subList(startIndex, endIndex);
            }

            // Récupérer les villes disponibles pour le filtre
            Set<String> villes = allCourses.stream()
                .map(Courses::getLieu)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

            // Récupérer les distances disponibles pour le filtre
            Set<Double> distances = allCourses.stream()
                .map(Courses::getDistance)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

            // Préparer les données pour le template
            Map<String, Object> data = new HashMap<>();
            data.put("courses", paginatedCourses);
            data.put("currentPage", page);
            data.put("totalPages", totalPages);
            data.put("totalCourses", totalCourses);
            data.put("hasNext", page < totalPages);
            data.put("hasPrevious", page > 1);
            data.put("utilisateurConnecte", utilisateurConnecte);
            data.put("pageTitle", "Courses ColorRun");
            data.put("courseImages", courseImages);
            
            // Données pour les filtres
            data.put("villes", villes.stream().sorted().collect(Collectors.toList()));
            data.put("distances", distances.stream().sorted().collect(Collectors.toList()));
            data.put("selectedDate", req.getParameter("dateFilter"));
            data.put("selectedVille", req.getParameter("villeFilter"));
            data.put("selectedDistance", req.getParameter("distanceFilter"));
            data.put("selectedSort", sortBy);
            data.put("searchQuery", req.getParameter("search"));

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

            TemplateUtil.processTemplate("courses", req, resp, data);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des courses: " + e.getMessage());
            e.printStackTrace();
            TemplateUtil.processTemplate("courses", req, resp, 
                Map.of("errorMessage", "Erreur lors du chargement des courses"));
        }
    }

    private List<Courses> applyFilters(List<Courses> courses, HttpServletRequest req) {
        return courses.stream()
            .filter(course -> applyDateFilter(course, req.getParameter("dateFilter")))
            .filter(course -> applyVilleFilter(course, req.getParameter("villeFilter")))
            .filter(course -> applyDistanceFilter(course, req.getParameter("distanceFilter")))
            .filter(course -> applySearchFilter(course, req.getParameter("search")))
            .collect(Collectors.toList());
    }

    private boolean applyDateFilter(Courses course, String dateFilter) {
        if (dateFilter == null || dateFilter.isEmpty()) {
            return true;
        }
        
        try {
            LocalDate filterDate = LocalDate.parse(dateFilter);
            LocalDate courseDate = course.getDateHeure().atZone(ZoneId.systemDefault()).toLocalDate();
            return courseDate.equals(filterDate);
        } catch (Exception e) {
            return true; // En cas d'erreur, ne pas filtrer
        }
    }

    private boolean applyVilleFilter(Courses course, String villeFilter) {
        if (villeFilter == null || villeFilter.isEmpty()) {
            return true;
        }
        
        return course.getLieu() != null && 
               course.getLieu().toLowerCase().contains(villeFilter.toLowerCase());
    }

    private boolean applyDistanceFilter(Courses course, String distanceFilter) {
        if (distanceFilter == null || distanceFilter.isEmpty()) {
            return true;
        }
        
        try {
            double filterDistance = Double.parseDouble(distanceFilter);
            return course.getDistance() != null && 
                   Math.abs(course.getDistance() - filterDistance) < 0.1; // Égalité avec tolérance
        } catch (Exception e) {
            return true;
        }
    }

    private boolean applySearchFilter(Courses course, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        
        String searchLower = search.toLowerCase().trim();
        return (course.getNomCourse() != null && course.getNomCourse().toLowerCase().contains(searchLower)) ||
               (course.getDescription() != null && course.getDescription().toLowerCase().contains(searchLower)) ||
               (course.getLieu() != null && course.getLieu().toLowerCase().contains(searchLower)) ||
               (course.getCauseSoutenue() != null && course.getCauseSoutenue().toLowerCase().contains(searchLower));
    }

    private List<Courses> applySorting(List<Courses> courses, String sortBy) {
        switch (sortBy) {
            case "date":
                return courses.stream()
                    .sorted(Comparator.comparing(Courses::getDateHeure))
                    .collect(Collectors.toList());
            case "ville":
                return courses.stream()
                    .sorted(Comparator.comparing(c -> c.getLieu() != null ? c.getLieu() : ""))
                    .collect(Collectors.toList());
            case "distance":
                return courses.stream()
                    .sorted(Comparator.comparing(c -> c.getDistance() != null ? c.getDistance() : 0.0))
                    .collect(Collectors.toList());
            case "prix":
                return courses.stream()
                    .sorted(Comparator.comparing(c -> c.getPrix() != null ? c.getPrix() : 0.0))
                    .collect(Collectors.toList());
            case "nom":
                return courses.stream()
                    .sorted(Comparator.comparing(c -> c.getNomCourse() != null ? c.getNomCourse() : ""))
                    .collect(Collectors.toList());
            default:
                return courses; // Aucun tri
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