package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.DemandeContact;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.DemandeContactRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DemandeContactRepositoryImpl implements DemandeContactRepository {

    @Override
    public void save(DemandeContact demande) {
        String sql = "INSERT INTO demandes_contact (nom, email, sujet, message, date_creation, traite) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, demande.getNom());
            stmt.setString(2, demande.getEmail());
            stmt.setString(3, demande.getSujet());
            stmt.setString(4, demande.getMessage());
            stmt.setTimestamp(5, Timestamp.from(demande.getDateCreation()));
            stmt.setBoolean(6, demande.isTraite());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    demande.setId(generatedKeys.getInt(1));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la demande de contact", e);
        }
    }

    @Override
    public List<DemandeContact> findAll() {
        String sql = "SELECT * FROM demandes_contact ORDER BY date_creation DESC";
        List<DemandeContact> demandes = new ArrayList<>();
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeContact(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes de contact", e);
        }
        
        return demandes;
    }

    @Override
    public DemandeContact findById(int id) {
        String sql = "SELECT * FROM demandes_contact WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDemandeContact(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la demande de contact", e);
        }
        
        return null;
    }

    @Override
    public void updateStatut(int id, boolean traite, String reponse) {
        String sql = "UPDATE demandes_contact SET traite = ?, reponse = ?, date_traitement = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setBoolean(1, traite);
            stmt.setString(2, reponse);
            stmt.setTimestamp(3, traite ? Timestamp.from(Instant.now()) : null);
            stmt.setInt(4, id);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut", e);
        }
    }

    @Override
    public List<DemandeContact> findByStatut(boolean traite) {
        String sql = "SELECT * FROM demandes_contact WHERE traite = ? ORDER BY date_creation DESC";
        List<DemandeContact> demandes = new ArrayList<>();
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setBoolean(1, traite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapResultSetToDemandeContact(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes par statut", e);
        }
        
        return demandes;
    }

    private DemandeContact mapResultSetToDemandeContact(ResultSet rs) throws SQLException {
        DemandeContact demande = new DemandeContact();
        demande.setId(rs.getInt("id"));
        demande.setNom(rs.getString("nom"));
        demande.setEmail(rs.getString("email"));
        demande.setSujet(rs.getString("sujet"));
        demande.setMessage(rs.getString("message"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            demande.setDateCreation(dateCreation.toInstant());
        }
        
        demande.setTraite(rs.getBoolean("traite"));
        demande.setReponse(rs.getString("reponse"));
        
        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        if (dateTraitement != null) {
            demande.setDateTraitement(dateTraitement.toInstant());
        }
        
        return demande;
    }
} 