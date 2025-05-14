package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Inscription;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.InscriptionRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscriptionRepositoryImpl implements InscriptionRepository {
    @Override
    public void create(Inscription inscription) {
        String query = "INSERT INTO INSCRIPTIONS (ID_INSCRIPTION, ID_COURSE, ID_UTILISATEUR, DOSSARD, QR_CODE, DATE_INSCRIPTION) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, inscription.getId());
            stmt.setInt(2, inscription.getCourse().getId());
            stmt.setInt(3, inscription.getUtilisateur().getId());
            stmt.setString(4, inscription.getDossard());
            stmt.setString(5, inscription.getQrCode());
            stmt.setObject(6, inscription.getDateInscription());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Inscription findById(Integer id) {
        String query = "SELECT * FROM INSCRIPTIONS WHERE ID_INSCRIPTION = ?";
        try (var conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new Inscription(
                    rs.getInt("ID_INSCRIPTION"),
                    rs.getObject("ID_COURSE", fr.esgi.robin.colorrun.business.Courses.class),
                    rs.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    rs.getString("DOSSARD"),
                    rs.getString("QR_CODE"),
                    rs.getObject("DATE_INSCRIPTION", java.time.Instant.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Inscription> findAll() {
        List<Inscription> inscriptions = new ArrayList<>();
        String query = "SELECT * FROM INSCRIPTIONS";
        try (Connection conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                inscriptions.add(new Inscription(
                    rs.getInt("ID_INSCRIPTION"),
                    rs.getObject("ID_COURSE", fr.esgi.robin.colorrun.business.Courses.class),
                    rs.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    rs.getString("DOSSARD"),
                    rs.getString("QR_CODE"),
                    rs.getObject("DATE_INSCRIPTION", java.time.Instant.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inscriptions;
    }

    @Override
    public void update(Inscription inscription) {
        String query = "UPDATE INSCRIPTIONS SET ID_COURSE = ?, ID_UTILISATEUR = ?, DOSSARD = ?, QR_CODE = ?, DATE_INSCRIPTION = ? WHERE ID_INSCRIPTION = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, inscription.getCourse().getId());
            stmt.setInt(2, inscription.getUtilisateur().getId());
            stmt.setString(3, inscription.getDossard());
            stmt.setString(4, inscription.getQrCode());
            stmt.setObject(5, inscription.getDateInscription());
            stmt.setInt(6, inscription.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Inscription inscription) {
        String query = "DELETE FROM INSCRIPTIONS WHERE ID_INSCRIPTION = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, inscription.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
