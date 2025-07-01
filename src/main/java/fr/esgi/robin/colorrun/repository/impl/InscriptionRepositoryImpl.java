package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.InscriptionRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InscriptionRepositoryImpl implements InscriptionRepository {
    @Override
    public void create(Inscription inscription) {
        String query = "INSERT INTO INSCRIPTIONS (ID_COURSE, ID_UTILISATEUR, DOSSARD, QR_CODE, DATE_INSCRIPTION) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, inscription.getCourse().getId());
            stmt.setInt(2, inscription.getUtilisateur().getId());
            stmt.setString(3, inscription.getDossard());
            stmt.setString(4, inscription.getQrCode());
            stmt.setObject(5, inscription.getDateInscription());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Inscription findById(Integer id) {
        String query = "SELECT * FROM INSCRIPTIONS WHERE ID_INSCRIPTION = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // TODO: Implémenter le mapping complet avec les objets Course et Utilisateur
                Inscription inscription = new Inscription();
                inscription.setId(rs.getInt("ID_INSCRIPTION"));
                inscription.setDossard(rs.getString("DOSSARD"));
                inscription.setQrCode(rs.getString("QR_CODE"));
                inscription.setDateInscription(rs.getObject("DATE_INSCRIPTION", java.time.Instant.class));
                return inscription;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Inscription> findAll() {
        List<Inscription> inscriptions = new ArrayList<>();
        String query = "SELECT * FROM INSCRIPTIONS";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // TODO: Implémenter le mapping complet
                Inscription inscription = new Inscription();
                inscription.setId(rs.getInt("ID_INSCRIPTION"));
                inscription.setDossard(rs.getString("DOSSARD"));
                inscription.setQrCode(rs.getString("QR_CODE"));
                inscription.setDateInscription(rs.getObject("DATE_INSCRIPTION", java.time.Instant.class));
                inscriptions.add(inscription);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des inscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return inscriptions;
    }

    @Override
    public void update(Inscription inscription) {
        String query = "UPDATE INSCRIPTIONS SET ID_COURSE = ?, ID_UTILISATEUR = ?, DOSSARD = ?, QR_CODE = ?, DATE_INSCRIPTION = ? WHERE ID_INSCRIPTION = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, inscription.getCourse().getId());
            stmt.setInt(2, inscription.getUtilisateur().getId());
            stmt.setString(3, inscription.getDossard());
            stmt.setString(4, inscription.getQrCode());
            stmt.setObject(5, inscription.getDateInscription());
            stmt.setInt(6, inscription.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Inscription inscription) {
        String query = "DELETE FROM INSCRIPTIONS WHERE ID_INSCRIPTION = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, inscription.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vérifier si un utilisateur est déjà inscrit à une course
     */
    public boolean existsByUserAndCourse(Integer userId, Integer courseId) {
        String query = "SELECT COUNT(*) FROM INSCRIPTIONS WHERE ID_UTILISATEUR = ? AND ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'inscription: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Compter le nombre de participants inscrits à une course
     */
    public int countByCourse(Integer courseId) {
        String query = "SELECT COUNT(*) FROM INSCRIPTIONS WHERE ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des participants: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Trouver toutes les inscriptions d'un utilisateur avec les détails complets
     */
    public List<Inscription> findByUserId(Integer userId) {
        List<Inscription> inscriptions = new ArrayList<>();
        String query = """
            SELECT i.*, 
                   c.id_course, c.nom_course, c.description, c.date_heure, c.lieu, c.distance, c.prix, c.nb_max_participants, c.cause_soutenue,
                   u.id_utilisateur, u.nom, u.prenom, u.email
            FROM INSCRIPTIONS i
            JOIN COURSES c ON i.id_course = c.id_course
            JOIN UTILISATEURS u ON i.id_utilisateur = u.id_utilisateur
            WHERE i.id_utilisateur = ?
            ORDER BY i.date_inscription DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Inscription inscription = new Inscription();
                inscription.setId(rs.getInt("id_inscription"));
                inscription.setDossard(rs.getString("dossard"));
                inscription.setQrCode(rs.getString("qr_code"));
                inscription.setDateInscription(rs.getTimestamp("date_inscription").toInstant());
                
                // Créer l'objet Course
                Courses course = new Courses();
                course.setId(rs.getInt("id_course"));
                course.setNomCourse(rs.getString("nom_course"));
                course.setDescription(rs.getString("description"));
                course.setDateHeure(rs.getTimestamp("date_heure").toInstant());
                course.setLieu(rs.getString("lieu"));
                course.setDistance(rs.getDouble("distance"));
                course.setPrix(rs.getDouble("prix"));
                course.setNbMaxParticipants(rs.getInt("nb_max_participants"));
                course.setCauseSoutenue(rs.getString("cause_soutenue"));
                
                // Créer l'objet Utilisateur
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id_utilisateur"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                
                inscription.setCourse(course);
                inscription.setUtilisateur(utilisateur);
                
                inscriptions.add(inscription);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des inscriptions par utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return inscriptions;
    }
}
