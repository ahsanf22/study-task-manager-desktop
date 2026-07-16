package it.unifi.ast.studytaskmanager.persistence;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.model.TaskStatus;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import jakarta.persistence.EntityManager;

public class JpaStudyTaskRepository implements StudyTaskRepository {

    private final EntityManagerProvider entityManagerProvider;

    public JpaStudyTaskRepository(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<StudyTask> findAll() {
        return entityManager()
                .createQuery("select t from StudyTask t join fetch t.category order by t.deadline", StudyTask.class)
                .getResultList();
    }

    @Override
    public Optional<StudyTask> findById(Long id) {
        return entityManager()
                .createQuery("select t from StudyTask t join fetch t.category where t.id = :id", StudyTask.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<StudyTask> findByCategoryId(Long categoryId) {
        return entityManager()
                .createQuery(
                        "select t from StudyTask t join fetch t.category where t.category.id = :categoryId order by t.deadline",
                        StudyTask.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<StudyTask> findByStatus(TaskStatus status) {
        return entityManager()
                .createQuery(
                        "select t from StudyTask t join fetch t.category where t.status = :status order by t.deadline",
                        StudyTask.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<StudyTask> findByTitleContaining(String title) {
        return entityManager()
                .createQuery(
                        "select t from StudyTask t join fetch t.category where lower(t.title) like lower(:title) order by t.deadline",
                        StudyTask.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        Long count = entityManager()
                .createQuery("select count(t) from StudyTask t where t.category.id = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public StudyTask save(StudyTask task) {
        if (task.getId() == null) {
            entityManager().persist(task);
            return task;
        }

        return entityManager().merge(task);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager()::remove);
    }

    private EntityManager entityManager() {
        return entityManagerProvider.getEntityManager();
    }
}
