package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Demandesorganisateur;
import fr.esgi.robin.colorrun.business.StatutDemande;
import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.DemandeOrganisateurRepository;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DemandeOrganisateurRepositoryImpl implements DemandeOrganisateurRepository {
    
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();

    @Override
    public void create(Demandesorganisateur demandeOrganisateur) {
        String query = "INSERT INTO DEMANDESORGANISATEUR (ID_UTILISATEUR, MOTIVATION, STATUT, DATE_DEMANDE) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, demandeOrganisateur.getUtilisateur().getId());
            stmt.setString(2, demandeOrganisateur.getMotivation());
            stmt.setString(3, demandeOrganisateur.getStatut().getStatut());
            stmt.setTimestamp(4, java.sql.Timestamp.from(demandeOrganisateur.getDateDemande()));
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création de la demande: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la demande", e);
        }
    }

    @Override
    public List<Demandesorganisateur> findAll() {
        List<Demandesorganisateur> demandes = new ArrayList<>();
        String query = "SELECT * FROM DEMANDESORGANISATEUR ORDER BY DATE_DEMANDE DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Demandesorganisateur demande = mapResultSetToEntity(rs);
                demandes.add(demande);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des demandes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return demandes;
    }

    @Override
    public Demandesorganisateur findById(Integer id) {
        String query = "SELECT * FROM DEMANDESORGANISATEUR WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de la demande: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public void update(Demandesorganisateur demandeOrganisateur) {
        String query = "UPDATE DEMANDESORGANISATEUR SET MOTIVATION = ?, STATUT = ?, DATE_DEMANDE = ? WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, demandeOrganisateur.getMotivation());
            stmt.setString(2, demandeOrganisateur.getStatut().getStatut());
            stmt.setTimestamp(3, java.sql.Timestamp.from(demandeOrganisateur.getDateDemande()));
            stmt.setInt(4, demandeOrganisateur.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la demande: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la demande", e);
        }
    }

    @Override
    public void delete(Demandesorganisateur demandeOrganisateur) {
        String query = "DELETE FROM DEMANDESORGANISATEUR WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, demandeOrganisateur.getId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la demande: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la demande", e);
        }
    }

    @Override
    public List<Demandesorganisateur> findByUtilisateur(Utilisateur utilisateur) {
        List<Demandesorganisateur> demandes = new ArrayList<>();
        String query = "SELECT * FROM DEMANDESORGANISATEUR WHERE ID_UTILISATEUR = ? ORDER BY DATE_DEMANDE DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, utilisateur.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Demandesorganisateur demande = mapResultSetToEntity(rs);
                    demandes.add(demande);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des demandes par utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return demandes;
    }

    @Override
    public List<Demandesorganisateur> findByStatut(StatutDemande statut) {
        List<Demandesorganisateur> demandes = new ArrayList<>();
        String query = "SELECT * FROM DEMANDESORGANISATEUR WHERE STATUT = ? ORDER BY DATE_DEMANDE DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, statut.getStatut());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Demandesorganisateur demande = mapResultSetToEntity(rs);
                    demandes.add(demande);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des demandes par statut: " + e.getMessage());
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    /**
     * Vérifier si un utilisateur a déjà une demande avec un statut donné
     */
    public boolean existsByUtilisateurAndStatut(Integer utilisateurId, StatutDemande statut) {
        String query = "SELECT COUNT(*) FROM DEMANDESORGANISATEUR WHERE ID_UTILISATEUR = ? AND STATUT = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setString(2, statut.getStatut());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification d'existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Demandesorganisateur mapResultSetToEntity(ResultSet rs) throws SQLException {
        Demandesorganisateur demande = new Demandesorganisateur();
        demande.setId(rs.getInt("ID"));
        demande.setMotivation(rs.getString("MOTIVATION"));
        
        // Conversion du statut depuis la base de données
        String statutBD = rs.getString("STATUT");
        StatutDemande statut = StatutDemande.fromStatut(statutBD);
        demande.setStatut(statut);
        
        demande.setDateDemande(rs.getTimestamp("DATE_DEMANDE").toInstant());
        
        // Récupérer l'utilisateur associé
        int utilisateurId = rs.getInt("ID_UTILISATEUR");
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);
        demande.setUtilisateur(utilisateur);
        
        return demande;
    }
}
