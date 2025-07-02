package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.business.DemandeContact;
import fr.esgi.robin.colorrun.repository.DemandeContactRepository;
import fr.esgi.robin.colorrun.repository.impl.DemandeContactRepositoryImpl;
import fr.esgi.robin.colorrun.util.TemplateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    
    private DemandeContactRepository demandeContactRepository = new DemandeContactRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Map<String, Object> model = new HashMap<>();
        
        // Ajouter les informations de contact
        model.put("pageTitle", "Contactez-nous");
        
        TemplateUtil.processTemplate("contact", request, response, model);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Récupération des données du formulaire
            String nom = request.getParameter("nom");
            String email = request.getParameter("email");
            String sujet = request.getParameter("sujet");
            String message = request.getParameter("message");
            
            // Validation simple
            if (nom == null || nom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                sujet == null || sujet.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {
                
                Map<String, Object> model = new HashMap<>();
                model.put("errorMessage", "Tous les champs sont obligatoires.");
                model.put("nom", nom);
                model.put("email", email);
                model.put("sujet", sujet);
                model.put("message", message);
                
                TemplateUtil.processTemplate("contact", request, response, model);
                return;
            }
            
            // Création et sauvegarde de la demande
            DemandeContact demande = new DemandeContact(nom.trim(), email.trim(), sujet.trim(), message.trim());
            demandeContactRepository.save(demande);
            
            System.out.println("📧 Nouvelle demande de contact reçue de: " + nom + " (" + email + ")");
            System.out.println("📝 Sujet: " + sujet);
            
            // Redirection avec message de succès
            response.sendRedirect(request.getContextPath() + "/contact?success=true");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du traitement de la demande de contact: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> model = new HashMap<>();
            model.put("errorMessage", "Une erreur est survenue. Veuillez réessayer.");
            
            TemplateUtil.processTemplate("contact", request, response, model);
        }
    }
} 