package fr.esgi.robin.colorrun.database;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebListener
public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("🗄️ Initialisation de la base de données...");
            
            // Exécuter le script d'initialisation
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("RUNSCRIPT FROM 'classpath:color_run_app_adapted.sql'");
                System.out.println("✅ Script SQL exécuté avec succès");
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de l'exécution du script (tables probablement existantes): " + e.getMessage());
            }
            
            // Vérifier si nous avons des courses
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Courses")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("📊 Nombre de courses dans la base: " + count);
                    
                    if (count == 0) {
                        System.out.println("⚠️ Aucune course trouvée, insertion des données d'exemple...");
                        insertSampleData(conn);
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'initialisation de la base: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertSampleData(Connection conn) throws Exception {
        // Insérer l'utilisateur admin si nécessaire
        try (PreparedStatement stmt = conn.prepareStatement(
            "MERGE INTO Utilisateurs (id_utilisateur, nom, prenom, email, mot_de_passe, role) " +
            "KEY(email) VALUES (1, 'Admin', 'ColorRun', 'admin@colorun.com', 'hashed_password', 'admin')")) {
            stmt.executeUpdate();
            System.out.println("✅ Utilisateur admin créé");
        }
        
        // Insérer les courses d'exemple
        String coursesQuery = "MERGE INTO Courses (id_course, nom_course, description, date_heure, lieu, distance, prix, nb_max_participants, cause_soutenue, organisateur_id) " +
                             "KEY(id_course) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(coursesQuery)) {
            // Course 1
            stmt.setInt(1, 1);
            stmt.setString(2, "ColorRun Paris");
            stmt.setString(3, "Une course colorée au cœur de la capitale française. Venez vivre une expérience unique dans les rues de Paris avec des milliers de participants !");
            stmt.setString(4, "2024-06-15 10:00:00");
            stmt.setString(5, "Paris, France");
            stmt.setDouble(6, 5.0);
            stmt.setDouble(7, 25.0);
            stmt.setInt(8, 500);
            stmt.setString(9, "Soutien aux enfants malades");
            stmt.setInt(10, 1);
            stmt.executeUpdate();
            
            // Course 2
            stmt.setInt(1, 2);
            stmt.setString(2, "ColorRun Lyon");
            stmt.setString(3, "Découvrez la ville des lumières sous un nouveau jour avec notre course colorée à travers Lyon. Un parcours magique vous attend !");
            stmt.setString(4, "2024-07-20 09:30:00");
            stmt.setString(5, "Lyon, France");
            stmt.setDouble(6, 3.5);
            stmt.setDouble(7, 20.0);
            stmt.setInt(8, 300);
            stmt.setString(9, "Protection de l'environnement");
            stmt.setInt(10, 1);
            stmt.executeUpdate();
            
            // Course 3
            stmt.setInt(1, 3);
            stmt.setString(2, "ColorRun Marseille");
            stmt.setString(3, "Course au bord de la Méditerranée avec une vue imprenable sur la mer. Courez dans un cadre idyllique !");
            stmt.setString(4, "2024-08-10 08:00:00");
            stmt.setString(5, "Marseille, France");
            stmt.setDouble(6, 7.0);
            stmt.setDouble(7, 30.0);
            stmt.setInt(8, 400);
            stmt.setString(9, "Aide aux personnes âgées");
            stmt.setInt(10, 1);
            stmt.executeUpdate();
            
            System.out.println("✅ 3 courses d'exemple insérées");
        }
    }
}
