package it.unifi.ast.studytaskmanager.persistence;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import jakarta.persistence.EntityManager;

public class JpaCategoryRepository implements CategoryRepository {

    private final EntityManagerProvider entityManagerProvider;

    public JpaCategoryRepository(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<Category> findAll() {
        return entityManager()
                .createQuery("select c from Category c order by c.name", Category.class)
                .getResultList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(entityManager().find(Category.class, id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        return entityManager()
                .createQuery("select c from Category c where c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = entityManager()
                .createQuery("select count(c) from Category c where c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            entityManager().persist(category);
            return category;
        }

        return entityManager().merge(category);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager()::remove);
    }

    private EntityManager entityManager() {
        return entityManagerProvider.getEntityManager();
    }
}
