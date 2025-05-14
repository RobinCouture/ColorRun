package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Demandesorganisateur;
import fr.esgi.robin.colorrun.business.StatutDemande;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.DemandeOrganisateurRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DemandeOrganisateurRepositoryImpl implements DemandeOrganisateurRepository {
    @Override
    public void create(Demandesorganisateur demandeOrganisateur) {
        String query = "INSERT INTO DEMANDESORGANISATEUR (ID_DEMANDE, ID_UTILISATEUR, ID_UTILISATEUR, STATUT, DATE_DEMANDE) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, demandeOrganisateur.getId());
            stmt.setInt(2, demandeOrganisateur.getUtilisateur().getId());
            stmt.setInt(3, demandeOrganisateur.getUtilisateur().getId());
            stmt.setObject(4, demandeOrganisateur.getStatut());
            stmt.setObject(5, demandeOrganisateur.getDateDemande());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Demandesorganisateur findById(Integer id) {
        String query = "SELECT * FROM DEMANDESORGANISATEUR WHERE ID_DEMANDE = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new Demandesorganisateur(
                    rs.getInt("ID_DEMANDE"),
                    rs.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    rs.getString("MOTIVATION"),
                    StatutDemande.valueOf(rs.getString("STATUT")),
                    rs.getObject("DATE_DEMANDE", java.time.Instant.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Demandesorganisateur> findAll() {
        List<Demandesorganisateur> demandesOrganisateur = new ArrayList<>();
        String query = "SELECT * FROM DEMANDESORGANISATEUR";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                demandesOrganisateur.add(new Demandesorganisateur(
                    rs.getInt("ID_DEMANDE"),
                    rs.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    rs.getString("MOTIVATION"),
                    StatutDemande.valueOf(rs.getString("STATUT")),
                    rs.getObject("DATE_DEMANDE", java.time.Instant.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return demandesOrganisateur;
    }

    @Override
    public void update(Demandesorganisateur demandeOrganisateur) {
        String query = "UPDATE DEMANDESORGANISATEUR SET ID_UTILISATEUR = ?, MOTIVATION = ?, STATUT = ?, DATE_DEMANDE = ? WHERE ID_DEMANDE = ?";
        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, demandeOrganisateur.getUtilisateur().getId());
            stmt.setString(2, demandeOrganisateur.getMotivation());
            stmt.setObject(3, demandeOrganisateur.getStatut());
            stmt.setObject(4, demandeOrganisateur.getDateDemande());
            stmt.setInt(5, demandeOrganisateur.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Demandesorganisateur demandeOrganisateur) {
        String query = "DELETE FROM DEMANDESORGANISATEUR WHERE ID_DEMANDE = ?";
        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, demandeOrganisateur.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
