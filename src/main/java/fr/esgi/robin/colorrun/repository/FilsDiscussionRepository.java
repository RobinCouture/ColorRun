package fr.esgi.robin.colorrun.repository;

import fr.esgi.robin.colorrun.business.FilsDiscussion;

import java.util.List;

public interface FilsDiscussionRepository {
    List<FilsDiscussion> findByCourseId(Integer courseId);
    FilsDiscussion save(FilsDiscussion filsDiscussion);
    void deleteById(Integer id);
}
