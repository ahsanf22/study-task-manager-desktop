package it.unifi.ast.studytaskmanager.persistence;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {

    EntityManager getEntityManager();
}
