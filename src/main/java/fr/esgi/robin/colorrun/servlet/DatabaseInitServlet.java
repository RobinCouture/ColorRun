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
import java.sql.Statement;

@WebServlet("/admin/init-db")
public class DatabaseInitServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><head><title>Initialisation Base de Données</title></head><body>");
        out.println("<h1>Initialisation des données de test</h1>");
        
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            
            // Script d'insertion
            String[] insertQueries = {
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Dupont', 'Marie', 'marie.dupont@admin.com', 'admin123', 'admin', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Martin', 'Pierre', 'pierre.martin@admin.com', 'admin123', 'admin', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Bernard', 'Sophie', 'sophie.bernard@organisateur.com', 'org123', 'organisateur', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Petit', 'Julien', 'julien.petit@organisateur.com', 'org123', 'organisateur', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Moreau', 'Emma', 'emma.moreau@participant.com', 'user123', 'participant', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Robert', 'Lucas', 'lucas.robert@participant.com', 'user123', 'participant', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Durand', 'Léa', 'lea.durand@test.com', 'test123', 'participant', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Leroy', 'Hugo', 'hugo.leroy@test.com', 'test123', 'participant', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Moreau', 'Chloé', 'chloe.moreau@test.com', 'test123', 'organisateur', CURRENT_TIMESTAMP)",
                "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, ROLE, DATE_INSCRIPTION) VALUES ('Simon', 'Nathan', 'nathan.simon@test.com', 'test123', 'participant', CURRENT_TIMESTAMP)"
            };
            
            out.println("<h2>Exécution des requêtes...</h2>");
            
            int count = 0;
            for (String query : insertQueries) {
                try {
                    statement.execute(query);
                    count++;
                    out.println("<p style='color: green;'>✅ Utilisateur créé</p>");
                } catch (Exception e) {
                    out.println("<p style='color: orange;'>⚠️ Utilisateur probablement déjà existant: " + e.getMessage() + "</p>");
                }
            }
            
            // Compter les utilisateurs
            var rs = statement.executeQuery("SELECT COUNT(*) FROM UTILISATEURS");
            if (rs.next()) {
                out.println("<h2>✅ Terminé !</h2>");
                out.println("<p><strong>Nombre total d'utilisateurs en base: " + rs.getInt(1) + "</strong></p>");
            }
            
            out.println("<p><a href='/ColorRun/admin/roles'>→ Aller à la gestion des rôles</a></p>");
            
        } catch (Exception e) {
            out.println("<h2 style='color: red;'>❌ Erreur</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("</body></html>");
    }
} 