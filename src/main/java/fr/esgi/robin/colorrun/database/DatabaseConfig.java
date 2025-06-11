package fr.esgi.robin.colorrun.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties properties = new Properties();
    private static String fallbackUrl = "jdbc:h2:file:./resources/database/ColorRun_Prod;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
    
    static {
        try {
            // Essayer de charger application.properties
            var inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties");
            if (inputStream != null) {
                properties.load(inputStream);
                System.out.println("✅ Configuration DB chargée depuis application.properties");
            } else {
                System.out.println("⚠️ application.properties non trouvé, utilisation de la configuration par défaut");
                // Configuration par défaut
                properties.setProperty("db.url", fallbackUrl);
                properties.setProperty("db.user", "sa");
                properties.setProperty("db.password", "");
                properties.setProperty("db.driver", "org.h2.Driver");
            }
            
            Class.forName(properties.getProperty("db.driver"));
            System.out.println("✅ Driver H2 chargé: " + properties.getProperty("db.url"));
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du chargement de la configuration DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        if (url == null || url.isEmpty()) {
            url = fallbackUrl;
            System.out.println("⚠️ URL DB null, utilisation du fallback: " + url);
        }
        
        return DriverManager.getConnection(url,
                properties.getProperty("db.user", "sa"),
                properties.getProperty("db.password", ""));
    }
}