package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.DemandeContact;
import java.util.List;

public interface DemandeContactRepository {
    void save(DemandeContact demande);
    List<DemandeContact> findAll();
    DemandeContact findById(int id);
    void updateStatut(int id, boolean traite, String reponse);
    List<DemandeContact> findByStatut(boolean traite);
} 