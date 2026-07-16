package it.unifi.ast.studytaskmanager.config;

import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import it.unifi.ast.studytaskmanager.gui.MainFrame;
import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerPanel;
import it.unifi.ast.studytaskmanager.persistence.JpaCategoryRepository;
import it.unifi.ast.studytaskmanager.persistence.JpaStudyTaskRepository;
import it.unifi.ast.studytaskmanager.persistence.JpaTransactionManager;
import it.unifi.ast.studytaskmanager.persistence.ThreadLocalEntityManagerProvider;
import it.unifi.ast.studytaskmanager.presenter.StudyTaskManagerPresenter;
import it.unifi.ast.studytaskmanager.service.CategoryService;
import it.unifi.ast.studytaskmanager.service.StudyTaskService;

public class ApplicationConfiguration implements AutoCloseable {

    private static final String PERSISTENCE_UNIT_NAME = "study-task-manager";

    private final EntityManagerFactory entityManagerFactory;

    private ApplicationConfiguration(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public static ApplicationConfiguration create() {
        Map<String, String> properties = ApplicationDatabaseProperties
                .fromEnvironment()
                .toJpaProperties();

        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

        return new ApplicationConfiguration(entityManagerFactory);
    }

    public MainFrame createMainFrame() {
        ThreadLocalEntityManagerProvider entityManagerProvider = new ThreadLocalEntityManagerProvider();

        JpaTransactionManager transactionManager =
                new JpaTransactionManager(entityManagerFactory, entityManagerProvider);

        JpaCategoryRepository categoryRepository =
                new JpaCategoryRepository(entityManagerProvider);

        JpaStudyTaskRepository studyTaskRepository =
                new JpaStudyTaskRepository(entityManagerProvider);

        CategoryService categoryService =
                new CategoryService(transactionManager, categoryRepository, studyTaskRepository);

        StudyTaskService studyTaskService =
                new StudyTaskService(transactionManager, studyTaskRepository, categoryRepository);

        StudyTaskManagerPanel panel = new StudyTaskManagerPanel();

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(panel, categoryService, studyTaskService);

        presenter.loadInitialData();

        return new MainFrame(panel);
    }

    @Override
    public void close() {
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
