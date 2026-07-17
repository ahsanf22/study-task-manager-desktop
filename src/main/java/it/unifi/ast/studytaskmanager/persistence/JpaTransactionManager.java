package it.unifi.ast.studytaskmanager.persistence;

import java.util.function.Supplier;

import it.unifi.ast.studytaskmanager.coverage.GeneratedCoverageExclusion;
import it.unifi.ast.studytaskmanager.transaction.TransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class JpaTransactionManager implements TransactionManager {

    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocalEntityManagerProvider entityManagerProvider;

    public JpaTransactionManager(
            EntityManagerFactory entityManagerFactory,
            ThreadLocalEntityManagerProvider entityManagerProvider) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public <T> T doInTransaction(Supplier<T> operation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            entityManagerProvider.bind(entityManager);
            transaction.begin();

            T result = operation.get();

            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            rollbackIfActive(transaction);
            throw exception;
        } finally {
            entityManagerProvider.unbind();
            entityManager.close();
        }
    }

    @GeneratedCoverageExclusion
    private void rollbackIfActive(EntityTransaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}
