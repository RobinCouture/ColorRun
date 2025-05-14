package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Filsdiscussion;
import fr.esgi.robin.colorrun.database.DatabaseConfig;
import fr.esgi.robin.colorrun.repository.FilsDiscussionRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilsDiscussionRepositoryImpl implements FilsDiscussionRepository {
    @Override
    public void create(Filsdiscussion filsDiscussion) {
        String query = "INSERT INTO FILSDISCUSSION (ID_MESSAGE, ID_COURSE, ID_UTILISATEUR, CONTENU, DATE_ENVOI) VALUES (?, ?, ?, ?, ?)";
        try (var connection = DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, filsDiscussion.getId());
            preparedStatement.setObject(2, filsDiscussion.getCourse());
            preparedStatement.setObject(3, filsDiscussion.getUtilisateur());
            preparedStatement.setString(4, filsDiscussion.getContenu());
            preparedStatement.setObject(5, filsDiscussion.getDateEnvoi());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filsdiscussion findById(Integer id) {
        String query = "SELECT * FROM FILSDISCUSSION WHERE ID_MESSAGE = ?";
        try (var connection = DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Filsdiscussion(
                    resultSet.getInt("ID_MESSAGE"),
                    resultSet.getObject("ID_COURSE", fr.esgi.robin.colorrun.business.Courses.class),
                    resultSet.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    resultSet.getString("CONTENU"),
                    resultSet.getObject("DATE_ENVOI", java.time.Instant.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Filsdiscussion> findAll() {
        List<Filsdiscussion> filsDiscussions = new ArrayList<>();
        String query = "SELECT * FROM FILSDISCUSSION";
        try (var connection = DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                filsDiscussions.add(new Filsdiscussion(
                    resultSet.getInt("ID_MESSAGE"),
                    resultSet.getObject("ID_COURSE", fr.esgi.robin.colorrun.business.Courses.class),
                    resultSet.getObject("ID_UTILISATEUR", fr.esgi.robin.colorrun.business.Utilisateur.class),
                    resultSet.getString("CONTENU"),
                    resultSet.getObject("DATE_ENVOI", java.time.Instant.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filsDiscussions;
    }

    @Override
    public void update(Filsdiscussion filsDiscussion) {
        String query = "UPDATE FILSDISCUSSION SET ID_COURSE = ?, ID_UTILISATEUR = ?, CONTENU = ?, DATE_ENVOI = ? WHERE ID_MESSAGE = ?";
        try (var connection = DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, filsDiscussion.getCourse());
            preparedStatement.setObject(2, filsDiscussion.getUtilisateur());
            preparedStatement.setString(3, filsDiscussion.getContenu());
            preparedStatement.setObject(4, filsDiscussion.getDateEnvoi());
            preparedStatement.setInt(5, filsDiscussion.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Filsdiscussion filsDiscussion) {
        String query = "DELETE FROM FILSDISCUSSION WHERE ID_MESSAGE = ?";
        try (var connection = DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, filsDiscussion.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
