package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.Filsdiscussion;

import java.util.List;

public interface FilsDiscussionRepository {
    void create(Filsdiscussion filsDiscussion);
    Filsdiscussion findById(Integer id);
    List<Filsdiscussion> findAll();
    void update(Filsdiscussion filsDiscussion);
    void delete(Filsdiscussion filsDiscussion);
}
