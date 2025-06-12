package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Courses;

import java.util.List;

public interface CoursesRepository {
    public void create(Courses course);
    public Courses findById(Integer id);
    public List<Courses> findAll();
    public List<Courses> findAllPaginated(int offset, int limit);
    public int countAll();
    public void update(Courses course);
    public void delete(Courses course);
}