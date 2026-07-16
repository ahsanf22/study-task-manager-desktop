package it.unifi.ast.studytaskmanager.repository;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.model.TaskStatus;

public interface StudyTaskRepository {

    List<StudyTask> findAll();

    Optional<StudyTask> findById(Long id);

    List<StudyTask> findByCategoryId(Long categoryId);

    List<StudyTask> findByStatus(TaskStatus status);

    List<StudyTask> findByTitleContaining(String title);

    boolean existsByCategoryId(Long categoryId);

    StudyTask save(StudyTask task);

    void deleteById(Long id);
}
