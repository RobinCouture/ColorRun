package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.CoursesRepository;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CoursesRepositoryImpl implements CoursesRepository {
    
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();
    
    @Override
    public void create(Courses course) {
        String query = "INSERT INTO COURSES (NOM_COURSE, DESCRIPTION, DATE_HEURE, LIEU, DISTANCE, PRIX, NB_MAX_PARTICIPANTS, CAUSE_SOUTENUE, ORGANISATEUR_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, course.getNomCourse());
            stmt.setString(2, course.getDescription());
            stmt.setObject(3, course.getDateHeure());
            stmt.setString(4, course.getLieu());
            stmt.setDouble(5, course.getDistance());
            stmt.setDouble(6, course.getPrix());
            stmt.setInt(7, course.getNbMaxParticipants());
            stmt.setString(8, course.getCauseSoutenue());
            stmt.setObject(9, course.getUtilisateur() != null ? course.getUtilisateur().getId() : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création de la course a échoué, aucune ligne affectée.");
            }

            // Récupérer l'ID généré automatiquement
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    course.setId(rs.getInt(1));
                    System.out.println("✅ Course créée avec ID: " + course.getId());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la course: " + e.getMessage());
            throw new RuntimeException("Impossible de créer la course", e);
        }
    }

    @Override
    public Courses findById(Integer id) {
        String query = "SELECT * FROM COURSES WHERE ID_COURSE = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la course: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Courses> findAll() {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT * FROM COURSES ORDER BY DATE_HEURE DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            var rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des courses: " + e.getMessage());
        }
        
        return courses;
    }

    @Override
    public List<Courses> findAllPaginated(int offset, int limit) {
        List<Courses> courses = new ArrayList<>();
        String query = "SELECT * FROM COURSES ORDER BY DATE_HEURE DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            var rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération paginée des courses: " + e.getMessage());
        }
        
        return courses;
    }

    @Override
    public int countAll() {
        String query = "SELECT COUNT(*) FROM COURSES";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des courses: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void update(Courses course) {
        String query = "UPDATE COURSES SET NOM_COURSE = ?, DESCRIPTION = ?, DATE_HEURE = ?, LIEU = ?, DISTANCE = ?, PRIX = ?, NB_MAX_PARTICIPANTS = ?, CAUSE_SOUTENUE = ? WHERE ID_COURSE = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
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
            System.err.println("Erreur lors de la mise à jour de la course: " + e.getMessage());
            throw new RuntimeException("Impossible de mettre à jour la course", e);
        }
    }

    @Override
    public void delete(Courses course) {
        String query = "DELETE FROM COURSES WHERE ID_COURSE = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, course.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la course: " + e.getMessage());
            throw new RuntimeException("Impossible de supprimer la course", e);
        }
    }
    
    /**
     * Méthode helper pour mapper un ResultSet vers un objet Courses
     */
    private Courses mapResultSetToCourse(ResultSet rs) throws SQLException {
        Courses course = new Courses();
        course.setId(rs.getInt("ID_COURSE"));
        course.setNomCourse(rs.getString("NOM_COURSE"));
        course.setDescription(rs.getString("DESCRIPTION"));
        course.setDateHeure(rs.getTimestamp("DATE_HEURE").toInstant());
        course.setLieu(rs.getString("LIEU"));
        course.setDistance(rs.getDouble("DISTANCE"));
        course.setPrix(rs.getDouble("PRIX"));
        course.setNbMaxParticipants(rs.getInt("NB_MAX_PARTICIPANTS"));
        course.setCauseSoutenue(rs.getString("CAUSE_SOUTENUE"));
        
        // Récupérer l'IMAGE_URL si elle existe
        try {
            course.setImageUrl(rs.getString("IMAGE_URL"));
        } catch (SQLException e) {
            // La colonne IMAGE_URL n'existe pas encore, on ignore
            course.setImageUrl(null);
        }
        
        // Récupérer l'organisateur associé
        Integer organisateurId = rs.getObject("ORGANISATEUR_ID", Integer.class);
        if (organisateurId != null) {
            Utilisateur organisateur = utilisateurRepository.findById(organisateurId);
            course.setUtilisateur(organisateur);
        }
        
        return course;
    }
}