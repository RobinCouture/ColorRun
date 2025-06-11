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

@WebServlet("/admin/debug-db")
public class DebugServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><head><title>Debug Base de Données</title></head><body>");
        out.println("<h1>🔍 Debug Base de Données</h1>");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            out.println("<h2>✅ Connexion OK</h2>");
            
            // Test 1: Compter tous les utilisateurs
            PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) FROM UTILISATEURS");
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                int total = countRs.getInt(1);
                out.println("<p><strong>Nombre total d'utilisateurs:</strong> " + total + "</p>");
            }
            
            // Test 2: Lister tous les utilisateurs
            PreparedStatement listStmt = conn.prepareStatement("SELECT ID_UTILISATEUR, EMAIL, NOM, PRENOM, ROLE FROM UTILISATEURS ORDER BY DATE_INSCRIPTION DESC");
            ResultSet listRs = listStmt.executeQuery();
            
            out.println("<h2>📋 Liste de tous les utilisateurs:</h2>");
            out.println("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            out.println("<tr><th>ID</th><th>Email</th><th>Nom</th><th>Prénom</th><th>Rôle</th></tr>");
            
            while (listRs.next()) {
                out.println("<tr>");
                out.println("<td>" + listRs.getInt("ID_UTILISATEUR") + "</td>");
                out.println("<td>" + listRs.getString("EMAIL") + "</td>");
                out.println("<td>" + listRs.getString("NOM") + "</td>");
                out.println("<td>" + listRs.getString("PRENOM") + "</td>");
                out.println("<td>" + listRs.getString("ROLE") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            // Test 3: Test de pagination
            out.println("<h2>🔍 Test de pagination (LIMIT 5 OFFSET 0):</h2>");
            PreparedStatement pageStmt = conn.prepareStatement("SELECT * FROM UTILISATEURS ORDER BY DATE_INSCRIPTION DESC LIMIT 5 OFFSET 0");
            ResultSet pageRs = pageStmt.executeQuery();
            
            int count = 0;
            while (pageRs.next()) {
                count++;
                out.println("<p>" + count + ". " + pageRs.getString("EMAIL") + " (" + pageRs.getString("ROLE") + ")</p>");
            }
            
            if (count == 0) {
                out.println("<p style='color: red;'>❌ Aucun résultat avec la pagination !</p>");
            } else {
                out.println("<p style='color: green;'>✅ " + count + " utilisateurs trouvés avec pagination</p>");
            }
            
        } catch (Exception e) {
            out.println("<h2 style='color: red;'>❌ Erreur</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("<p><a href='/admin/roles'>← Retour à la gestion des rôles</a></p>");
        out.println("</body></html>");
    }
} 