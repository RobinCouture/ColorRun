package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Inscription;

import java.util.List;

public interface InscriptionRepository {
    void create(Inscription inscription);
    Inscription findById(Integer id);
    List<Inscription> findAll();
    void update(Inscription inscription);
    void delete(Inscription inscription);
}
