package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.repository.InscriptionRepository;
import fr.esgi.robin.colorrun.repository.impl.InscriptionRepositoryImpl;
import fr.esgi.robin.colorrun.util.PDFGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "downloadDossard", value = "/download-dossard/*")
public class DownloadDossardServlet extends HttpServlet {
    private final InscriptionRepository inscriptionRepository = new InscriptionRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Extraire l'ID de l'inscription depuis l'URL
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'inscription manquant");
                return;
            }

            Integer inscriptionId = Integer.parseInt(pathInfo.substring(1));
            
            // Récupérer l'inscription
            Inscription inscription = inscriptionRepository.findById(inscriptionId);
            if (inscription == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Inscription non trouvée");
                return;
            }

            // Vérifier que l'utilisateur est propriétaire de cette inscription ou admin
            if (!inscription.getUtilisateur().getId().equals(utilisateurConnecte.getId()) && 
                !utilisateurConnecte.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès interdit");
                return;
            }

            // Vérifier que l'inscription peut générer un PDF
            if (!PDFGenerator.canGeneratePDF(inscription)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Impossible de générer le dossard");
                return;
            }

            try {
                // Générer le PDF
                byte[] pdfBytes = PDFGenerator.generateDossardPDF(inscription);
                String fileName = PDFGenerator.generatePDFFileName(inscription);

                // Configurer la réponse pour le téléchargement
                resp.setContentType("application/pdf");
                resp.setContentLength(pdfBytes.length);
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                resp.setHeader("Pragma", "no-cache");
                resp.setHeader("Expires", "0");

                // Écrire le PDF dans la réponse
                resp.getOutputStream().write(pdfBytes);
                resp.getOutputStream().flush();

                System.out.println("📥 Téléchargement du dossard " + inscription.getDossard() + 
                                 " pour " + utilisateurConnecte.getNomComplet());

            } catch (Exception e) {
                System.err.println("Erreur lors de la génération du PDF: " + e.getMessage());
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur lors de la génération du dossard");
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID d'inscription invalide");
        } catch (Exception e) {
            System.err.println("Erreur lors du téléchargement du dossard: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
        }
    }
} 