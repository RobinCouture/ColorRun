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
        String query = "INSERT INTO UTILISATEURS (NOM, PRENOM, EMAIL, MOT_DE_PASSE, PHOTO_PROFIL, DATE_INSCRIPTION, ROLE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse());
            stmt.setString(5, utilisateur.getPhotoProfil());
            stmt.setObject(6, utilisateur.getDateInscription());
            stmt.setString(7, utilisateur.getRoleString());
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
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("ID_UTILISATEUR"),
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("EMAIL"),
                    rs.getString("MOT_DE_PASSE"),
                    rs.getString("PHOTO_PROFIL"),
                    rs.getObject("DATE_INSCRIPTION", Instant.class)
                );
                utilisateur.setRoleString(rs.getString("ROLE"));
                return utilisateur;
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
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("ID_UTILISATEUR"),
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("EMAIL"),
                    rs.getString("MOT_DE_PASSE"),
                    rs.getString("PHOTO_PROFIL"),
                    rs.getObject("DATE_INSCRIPTION", Instant.class)
                );
                utilisateur.setRoleString(rs.getString("ROLE"));
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String query = "UPDATE UTILISATEURS SET NOM = ?, PRENOM = ?, EMAIL = ?, MOT_DE_PASSE = ?, PHOTO_PROFIL = ?, DATE_INSCRIPTION = ?, ROLE = ? WHERE ID_UTILISATEUR = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse());
            stmt.setString(5, utilisateur.getPhotoProfil());
            stmt.setObject(6, utilisateur.getDateInscription());
            stmt.setString(7, utilisateur.getRoleString());
            stmt.setInt(8, utilisateur.getId());
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

    @Override
    public Utilisateur findByEmail(String email) {
        String query = "SELECT * FROM UTILISATEURS WHERE EMAIL = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                        rs.getInt("ID_UTILISATEUR"),
                        rs.getString("NOM"),
                        rs.getString("PRENOM"),
                        rs.getString("EMAIL"),
                        rs.getString("MOT_DE_PASSE"),
                        rs.getString("PHOTO_PROFIL"),
                        rs.getObject("DATE_INSCRIPTION", Instant.class)
                );
                utilisateur.setRoleString(rs.getString("ROLE"));
                return utilisateur;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAllPaginated(int offset, int limit) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM UTILISATEURS ORDER BY DATE_INSCRIPTION DESC LIMIT ? OFFSET ?";
        
        System.out.println("🔍 Requête SQL: " + query);
        System.out.println("📊 Paramètres: limit=" + limit + ", offset=" + offset);
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("ID_UTILISATEUR"),
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("EMAIL"),
                    rs.getString("MOT_DE_PASSE"),
                    rs.getString("PHOTO_PROFIL"),
                    rs.getObject("DATE_INSCRIPTION", Instant.class)
                );
                utilisateur.setRoleString(rs.getString("ROLE"));
                utilisateurs.add(utilisateur);
                System.out.println("👤 Utilisateur trouvé: " + utilisateur.getEmail() + " (" + utilisateur.getRoleString() + ")");
            }
            System.out.println("✅ Total utilisateurs récupérés: " + count);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération paginée des utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
        return utilisateurs;
    }

    @Override
    public int countAll() {
        String query = "SELECT COUNT(*) FROM UTILISATEURS";
        System.out.println("🔍 Comptage des utilisateurs...");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 Nombre total d'utilisateurs: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
