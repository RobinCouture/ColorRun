package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Demandesorganisateur;

import java.util.List;

public interface DemandeOrganisateurRepository {
    void create(Demandesorganisateur demandeOrganisateur);
    Demandesorganisateur findById(Integer id);
    List<Demandesorganisateur> findAll();
    void update(Demandesorganisateur demandeOrganisateur);
    void delete(Demandesorganisateur demandeOrganisateur);
}
