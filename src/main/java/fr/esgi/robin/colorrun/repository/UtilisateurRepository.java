package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Utilisateur;

import java.time.Instant;
import java.util.List;

public interface UtilisateurRepository {
    public void create(Utilisateur utilisateur);
    public Utilisateur findById(Integer id);
    public List<Utilisateur> findAll();
    public void update(Utilisateur utilisateur);
    public void delete(Utilisateur utilisateur);
    public Utilisateur findByEmail(String email);
    public Utilisateur findByResetToken(String resetToken);
    public void saveResetToken(Utilisateur utilisateur, String resetToken, Instant expirationTime);
    public Instant getResetTokenExpiration(String token);
    public void deleteResetToken(String resetToken);
    
    // Méthodes pour la pagination
    public List<Utilisateur> findAllPaginated(int offset, int limit);
    public int countAll();

    // Supprimer un utilisateur par son ID
    public void deleteById(int userId);

}
