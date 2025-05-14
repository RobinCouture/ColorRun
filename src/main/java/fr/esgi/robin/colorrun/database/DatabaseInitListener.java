package fr.esgi.robin.colorrun.database;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement()) {
            stmt.execute("RUNSCRIPT FROM 'classpath:color_run_app_adapted.sql'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
