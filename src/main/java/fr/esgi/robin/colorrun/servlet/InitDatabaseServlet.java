package fr.esgi.robin.colorrun.servlet;

import fr.esgi.robin.colorrun.database.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/admin/init-database")
public class InitDatabaseServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Initialisation Base de Données</title></head><body>");
        out.println("<h1>Initialisation de la table DemandesOrganisateur</h1>");
        
        try {
            createDemandesOrganisateurTable();
            out.println("<p style='color: green;'>✅ Table DemandesOrganisateur créée avec succès !</p>");
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Erreur: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("<p><a href='/admin/demandes-organisateur'>Aller à la gestion des demandes</a></p>");
        out.println("</body></html>");
    }
    
    private void createDemandesOrganisateurTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS DemandesOrganisateur (
                id_demande INT AUTO_INCREMENT PRIMARY KEY,
                id_utilisateur INT NOT NULL,
                motivation TEXT NOT NULL,
                statut ENUM('en attente', 'acceptée', 'refusée') DEFAULT 'en attente',
                date_demande DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_utilisateur) REFERENCES Utilisateurs(id_utilisateur) ON DELETE CASCADE,
                INDEX idx_statut_demandes (statut),
                INDEX idx_utilisateur_demandes (id_utilisateur)
            )
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            
            stmt.executeUpdate();
            System.out.println("Table DemandesOrganisateur créée avec succès");
        }
    }
} 