package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.CoursesRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CoursesRepositoryImpl implements CoursesRepository {
    @Override
    public void create(Courses course) {
        String query = "INSERT INTO COURSES (ID_COURSE, NOM_COURSE, DESCRIPTION, DATE_HEURE, LIEU, DISTANCE, PRIX, NB_MAX_PARTICIPANTS, CAUSE_SOUTENUE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, course.getId());
            stmt.setString(2, course.getNomCourse());
            stmt.setString(3, course.getDescription());
            stmt.setObject(4, course.getDateHeure());
            stmt.setString(5, course.getLieu());
            stmt.setDouble(6, course.getDistance());
            stmt.setDouble(7, course.getPrix());
            stmt.setInt(8, course.getNbMaxParticipants());
            stmt.setString(9, course.getCauseSoutenue());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Courses findById(Integer id) {
        String query = "SELECT * FROM COURSES WHERE ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("🔍 Recherche de la course avec ID: " + id); // DEBUG

            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Course trouvée: " + rs.getString("NOM_COURSE")); // DEBUG

                // Conversion sécurisée de la date
                java.sql.Timestamp timestamp = rs.getTimestamp("DATE_HEURE");
                Instant dateHeure = timestamp != null ? timestamp.toInstant() : Instant.now();

                return new Courses(
                        rs.getInt("ID_COURSE"),
                        rs.getString("NOM_COURSE"),
                        rs.getString("DESCRIPTION"),
                        dateHeure,
                        rs.getString("LIEU"),
                        rs.getDouble("DISTANCE"),
                        rs.getDouble("PRIX"),
                        rs.getInt("NB_MAX_PARTICIPANTS"),
                        rs.getString("CAUSE_SOUTENUE"),
                        null // On ignore l'organisateur pour l'instant
                );
            } else {
                System.out.println("❌ Aucune course trouvée avec l'ID: " + id); // DEBUG
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL dans findById: " + e.getMessage()); // DEBUG
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Courses> findAll() {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT * FROM COURSES";
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("🔍 Tentative de connexion à la base..."); // DEBUG
            var stmt = conn.prepareStatement(query);
            var rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("📋 Course trouvée #" + count + ": " + rs.getString("NOM_COURSE")); // DEBUG

                try {
                    // Conversion sécurisée de la date
                    java.sql.Timestamp timestamp = rs.getTimestamp("DATE_HEURE");
                    Instant dateHeure = timestamp != null ? timestamp.toInstant() : Instant.now();

                    // Récupération sécurisée de l'organisateur (peut être null)
                    Integer organisateurId = rs.getObject("ORGANISATEUR_ID", Integer.class);

                    Courses course = new Courses(
                            rs.getInt("ID_COURSE"),
                            rs.getString("NOM_COURSE"),
                            rs.getString("DESCRIPTION"),
                            dateHeure,
                            rs.getString("LIEU"),
                            rs.getDouble("DISTANCE"),
                            rs.getDouble("PRIX"),
                            rs.getInt("NB_MAX_PARTICIPANTS"),
                            rs.getString("CAUSE_SOUTENUE"),
                            null // On ignore l'organisateur pour l'instant
                    );

                    courses.add(course);
                    System.out.println("✅ Course ajoutée: " + course.getNomCourse()); // DEBUG

                } catch (Exception e) {
                    System.out.println("❌ Erreur lors de la création de la course #" + count + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("📊 Total courses récupérées: " + courses.size()); // DEBUG

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage()); // DEBUG
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public void update(Courses course) {
        String query = "UPDATE COURSES SET NOM_COURSE = ?, DESCRIPTION = ?, DATE_HEURE = ?, LIEU = ?, DISTANCE = ?, PRIX = ?, NB_MAX_PARTICIPANTS = ?, CAUSE_SOUTENUE = ? WHERE ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, course.getNomCourse());
            stmt.setString(2, course.getDescription());
            stmt.setObject(3, course.getDateHeure());
            stmt.setString(4, course.getLieu());
            stmt.setDouble(5, course.getDistance());
            stmt.setDouble(6, course.getPrix());
            stmt.setInt(7, course.getNbMaxParticipants());
            stmt.setString(8, course.getCauseSoutenue());
            stmt.setInt(9, course.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Courses course) {
        String query = "DELETE FROM COURSES WHERE ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, course.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
