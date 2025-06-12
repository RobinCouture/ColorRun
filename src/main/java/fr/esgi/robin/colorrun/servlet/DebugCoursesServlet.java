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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/debug-courses")
public class DebugCoursesServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><head><title>Debug Courses</title></head><body>");
        out.println("<h1>🔍 Debug Base de Données - Courses</h1>");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            out.println("<h2>✅ Connexion à la base réussie</h2>");
            
            // Vérifier la structure de la table
            out.println("<h3>📋 Structure de la table COURSES :</h3>");
            PreparedStatement descStmt = conn.prepareStatement("SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'COURSES'");
            ResultSet descRs = descStmt.executeQuery();
            out.println("<table border='1'><tr><th>Colonne</th><th>Type</th></tr>");
            while (descRs.next()) {
                out.println("<tr><td>" + descRs.getString("COLUMN_NAME") + "</td><td>" + descRs.getString("TYPE_NAME") + "</td></tr>");
            }
            out.println("</table>");
            
            // Compter le nombre total de courses
            PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) as total FROM COURSES");
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                int total = countRs.getInt("total");
                out.println("<h3>📊 Nombre total de courses : " + total + "</h3>");
            }
            
            // Afficher toutes les courses
            out.println("<h3>📝 Contenu de la table COURSES :</h3>");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM COURSES ORDER BY ID_COURSE DESC");
            ResultSet rs = stmt.executeQuery();
            
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Nom</th><th>Description</th><th>Date/Heure</th><th>Lieu</th><th>Distance</th><th>Prix</th><th>Max Participants</th><th>Cause</th><th>Organisateur ID</th></tr>");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("ID_COURSE") + "</td>");
                out.println("<td>" + rs.getString("NOM_COURSE") + "</td>");
                out.println("<td>" + rs.getString("DESCRIPTION") + "</td>");
                out.println("<td>" + rs.getTimestamp("DATE_HEURE") + "</td>");
                out.println("<td>" + rs.getString("LIEU") + "</td>");
                out.println("<td>" + rs.getDouble("DISTANCE") + "</td>");
                out.println("<td>" + rs.getDouble("PRIX") + "</td>");
                out.println("<td>" + rs.getInt("NB_MAX_PARTICIPANTS") + "</td>");
                out.println("<td>" + rs.getString("CAUSE_SOUTENUE") + "</td>");
                out.println("<td>" + rs.getObject("ORGANISATEUR_ID") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            if (!hasData) {
                out.println("<p style='color: red;'>❌ Aucune course trouvée dans la base de données</p>");
            }
            
        } catch (SQLException e) {
            out.println("<h2 style='color: red;'>❌ Erreur de connexion à la base :</h2>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }
        
        out.println("<br><a href='/courses'>← Retour aux courses</a>");
        out.println("</body></html>");
    }
} 