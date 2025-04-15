package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Utilisateur;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.UtilisateurRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {
    @Override
    public void create(Utilisateur utilisateur) {
        String query = "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, PHOTO_PROFIL, DATE_INSCRIPTION) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse());
            stmt.setString(5, utilisateur.getPhotoProfil());
            stmt.setObject(6, utilisateur.getDateInscription());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Utilisateur findById(Integer id) {
        String query = "SELECT * FROM UTILISATEURS WHERE ID_UTILISATEUR = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("ID_UTILISATEUR"),
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("EMAIL"),
                    rs.getString("MOT_DE_PASSE"),
                    rs.getString("PHOTO_PROFIL"),
                    rs.getObject("DATE_INSCRIPTION", Instant.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM UTILISATEURS";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                    rs.getInt("ID_UTILISATEUR"),
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("EMAIL"),
                    rs.getString("MOT_DE_PASSE"),
                    rs.getString("PHOTO_PROFIL"),
                    rs.getObject("DATE_INSCRIPTION", Instant.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String query = "UPDATE UTILISATEURS SET NOM = ?, PRENOM = ?, EMAIL = ?, MOT_DE_PASSE = ?, PHOTO_PROFIL = ?, DATE_INSCRIPTION = ? WHERE ID_UTILISATEUR = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse());
            stmt.setString(5, utilisateur.getPhotoProfil());
            stmt.setObject(6, utilisateur.getDateInscription());
            stmt.setInt(7, utilisateur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Utilisateur utilisateur) {
        String query = "DELETE FROM UTILISATEURS WHERE ID_UTILISATEUR = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, utilisateur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
