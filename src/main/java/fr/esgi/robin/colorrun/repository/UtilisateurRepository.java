package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Utilisateur;

import java.util.List;

public interface UtilisateurRepository {
    public void create(Utilisateur utilisateur);
    public Utilisateur findById(Integer id);
    public List<Utilisateur> findAll();
    public void update(Utilisateur utilisateur);
    public void delete(Utilisateur utilisateur);
    public Utilisateur findByEmail(String email);
    
    // Méthodes pour la pagination
    public List<Utilisateur> findAllPaginated(int offset, int limit);
    public int countAll();
}
