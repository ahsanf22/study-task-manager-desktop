package it.unifi.ast.studytaskmanager.persistence;

import jakarta.persistence.EntityManager;

public class ThreadLocalEntityManagerProvider implements EntityManagerProvider {

    private final ThreadLocal<EntityManager> currentEntityManager = new ThreadLocal<>();

    @Override
    public EntityManager getEntityManager() {
        EntityManager entityManager = currentEntityManager.get();

        if (entityManager == null) {
            throw new IllegalStateException("No EntityManager bound to the current transaction");
        }

        return entityManager;
    }

    void bind(EntityManager entityManager) {
        currentEntityManager.set(entityManager);
    }

    void unbind() {
        currentEntityManager.remove();
    }
}
