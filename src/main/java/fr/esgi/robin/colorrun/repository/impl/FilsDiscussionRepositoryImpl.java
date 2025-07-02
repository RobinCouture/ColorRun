package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.FilsDiscussion;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.FilsDiscussionRepository;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class FilsDiscussionRepositoryImpl implements FilsDiscussionRepository {

    @Override
    public List<FilsDiscussion> findByCourseId(Integer courseId) {
        List<FilsDiscussion> messages = new ArrayList<>();
        String query = """
            SELECT f.ID_MESSAGE, f.CONTENU, f.DATE_ENVOI,
                   u.ID_UTILISATEUR, u.NOM, u.PRENOM, u.EMAIL, u.ROLE,
                   c.ID_COURSE, c.NOM_COURSE
            FROM FILSDISCUSSION f
            JOIN UTILISATEURS u ON f.ID_UTILISATEUR = u.ID_UTILISATEUR
            JOIN COURSES c ON f.ID_COURSE = c.ID_COURSE
            WHERE f.ID_COURSE = ?
            ORDER BY f.DATE_ENVOI ASC
        """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, courseId);
            var rs = stmt.executeQuery();

            while (rs.next()) {
                // Créer l'utilisateur
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("ID_UTILISATEUR"));
                utilisateur.setNom(rs.getString("NOM"));
                utilisateur.setPrenom(rs.getString("PRENOM"));
                utilisateur.setEmail(rs.getString("EMAIL"));
                utilisateur.setRoleString(rs.getString("ROLE"));

                // Créer la course
                Courses course = new Courses();
                course.setId(rs.getInt("ID_COURSE"));
                course.setNomCourse(rs.getString("NOM_COURSE"));

                // Créer le message
                FilsDiscussion filsDiscussion = new FilsDiscussion();
                filsDiscussion.setId(rs.getInt("ID_MESSAGE"));
                filsDiscussion.setContenu(rs.getString("CONTENU"));
                filsDiscussion.setDateEnvoi(rs.getTimestamp("DATE_ENVOI").toInstant());
                filsDiscussion.setUtilisateur(utilisateur);
                filsDiscussion.setCourse(course);

                messages.add(filsDiscussion);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des messages: " + e.getMessage());
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public FilsDiscussion save(FilsDiscussion filsDiscussion) {
        String query = "INSERT INTO FILSDISCUSSION (CONTENU, DATE_ENVOI, ID_UTILISATEUR, ID_COURSE) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, filsDiscussion.getContenu());
            stmt.setTimestamp(2, Timestamp.from(filsDiscussion.getDateEnvoi()));
            stmt.setInt(3, filsDiscussion.getUtilisateur().getId());
            stmt.setInt(4, filsDiscussion.getCourse().getId());
            
            stmt.executeUpdate();
            
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                filsDiscussion.setId(rs.getInt(1));
            }
            
            System.out.println("✅ Message sauvegardé avec succès: " + filsDiscussion.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du message: " + e.getMessage());
            e.printStackTrace();
        }
        
        return filsDiscussion;
    }

    @Override
    public void deleteById(Integer id) {
        String query = "DELETE FROM FILSDISCUSSION WHERE ID_MESSAGE = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Message supprimé avec succès: " + id);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
