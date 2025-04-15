package fr.esgi.robin.colorrun.repository.impl;

import fr.esgi.robin.colorrun.business.Courses;
import fr.esgi.robin.colorrun.repository.CoursesRepository;

import java.util.List;

public class CoursesRepositoryImpl implements CoursesRepository {
    @Override
    public void create(Courses course) {
        String query = "INSERT INTO COURS (ID_COURS, NOM_COURS, DESCRIPTION, DATE_HEURE, LIEU, DISTANCE, PRIX, NB_MAX_PARTICIPANTS, CAUSE_SOUTENUE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public Courses findById(Integer id) {
        return null;
    }

    @Override
    public List<Courses> findAll() {
        return List.of();
    }

    @Override
    public void update(Courses course) {

    }

    @Override
    public void delete(Courses course) {

    }
}
