package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.InscriptionRepository;
import fr.esgi.robin.colorrun.repository.impl.CoursesRepositoryImpl;
import fr.esgi.robin.colorrun.repository.impl.InscriptionRepositoryImpl;
import fr.esgi.robin.colorrun.util.DossardGenerator;
import fr.esgi.robin.colorrun.util.QRCodeGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Instant;

@WebServlet(name = "inscription", urlPatterns = {"/inscription/*"})
public class InscriptionServlet extends HttpServlet {
    private final InscriptionRepository inscriptionRepository = new InscriptionRepositoryImpl();
    private final CoursesRepository coursesRepository = new CoursesRepositoryImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Extraire l'ID de la course depuis l'URL /course/4/inscription
            String requestURI = req.getRequestURI();
            String contextPath = req.getContextPath();
            String path = requestURI.substring(contextPath.length());
            
            System.out.println("🔍 URL complète: " + requestURI);
            System.out.println("🔍 Path après context: " + path);
            
            // Pattern: /course/4/inscription -> extraire le 4
            String[] pathParts = path.split("/");
            if (pathParts.length < 3 || !"inscription".equals(pathParts[1])) {
                System.out.println("❌ URL invalide: " + path);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL invalide");
                return;
            }

            Integer courseId = Integer.parseInt(pathParts[2]);
            System.out.println("✅ Course ID extrait: " + courseId);

            // Récupérer la course
            Courses course = coursesRepository.findById(courseId);
            if (course == null) {
                session.setAttribute("errorMessage", "Course non trouvée");
                resp.sendRedirect(req.getContextPath() + "/courses");
                return;
            }

            System.out.println("✅ Course trouvée: " + course.getNomCourse());

            // Vérifier si la course est encore ouverte aux inscriptions
            if (course.getDateHeure().isBefore(Instant.now())) {
                session.setAttribute("errorMessage", "Les inscriptions pour cette course sont fermées");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                return;
            }

            // Vérifier si l'utilisateur n'est pas déjà inscrit
            if (isUserAlreadyRegistered(utilisateurConnecte.getId(), courseId)) {
                session.setAttribute("errorMessage", "Vous êtes déjà inscrit à cette course");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                return;
            }

            // Vérifier s'il reste de la place
            int currentParticipants = getCurrentParticipantsCount(courseId);
            if (currentParticipants >= course.getNbMaxParticipants()) {
                session.setAttribute("errorMessage", "Cette course est complète");
                resp.sendRedirect(req.getContextPath() + "/course/" + courseId);
                return;
            }

            // Générer un numéro de dossard unique
            String numeroDossard = DossardGenerator.generateDossard(course, utilisateurConnecte);

            // Générer un QR code pour le dossard
            String qrCodeData = QRCodeGenerator.generateQRCodeData(
                course.getId().toString(), 
                utilisateurConnecte.getId().toString(), 
                numeroDossard
            );

            // Créer l'inscription
            Inscription inscription = new Inscription();
            inscription.setCourse(course);
            inscription.setUtilisateur(utilisateurConnecte);
            inscription.setDossard(numeroDossard);
            inscription.setQrCode(qrCodeData);
            inscription.setDateInscription(Instant.now());

            inscriptionRepository.create(inscription);

            System.out.println("✅ Inscription créée avec succès pour " + utilisateurConnecte.getNomComplet());

            session.setAttribute("successMessage", 
                "Inscription réussie ! Votre numéro de dossard est " + numeroDossard + 
                ". Rendez-vous dans votre profil pour télécharger votre dossard PDF.");

            // Rediriger vers la page de détails de la course
            resp.sendRedirect(req.getContextPath() + "/course/" + courseId);

        } catch (NumberFormatException e) {
            System.err.println("❌ ID de course invalide: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de course invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur lors de l'inscription. Veuillez réessayer.");
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    private boolean isUserAlreadyRegistered(Integer userId, Integer courseId) {
        try {
            // Ici on devrait avoir une méthode dans le repository pour vérifier
            // Pour l'instant, on fait une vérification basique
            InscriptionRepositoryImpl impl = (InscriptionRepositoryImpl) inscriptionRepository;
            return impl.existsByUserAndCourse(userId, courseId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification d'inscription: " + e.getMessage());
            return false;
        }
    }

    private int getCurrentParticipantsCount(Integer courseId) {
        try {
            InscriptionRepositoryImpl impl = (InscriptionRepositoryImpl) inscriptionRepository;
            return impl.countByCourse(courseId);
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des participants: " + e.getMessage());
            return 0;
        }
    }
} 