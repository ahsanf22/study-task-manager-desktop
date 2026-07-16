package it.unifi.ast.studytaskmanager.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.unifi.ast.studytaskmanager.exception.DuplicateCategoryNameException;
import it.unifi.ast.studytaskmanager.model.Priority;
import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.model.TaskStatus;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import it.unifi.ast.studytaskmanager.service.CategoryService;
import it.unifi.ast.studytaskmanager.service.StudyTaskService;
import it.unifi.ast.studytaskmanager.transaction.TransactionManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class JpaPersistenceIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    private EntityManagerFactory entityManagerFactory;
    private CategoryService categoryService;
    private StudyTaskService studyTaskService;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory(
                "study-task-manager",
                persistenceProperties());

        ThreadLocalEntityManagerProvider entityManagerProvider = new ThreadLocalEntityManagerProvider();
        TransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory, entityManagerProvider);

        CategoryRepository categoryRepository = new JpaCategoryRepository(entityManagerProvider);
        StudyTaskRepository studyTaskRepository = new JpaStudyTaskRepository(entityManagerProvider);

        categoryService = new CategoryService(transactionManager, categoryRepository, studyTaskRepository);
        studyTaskService = new StudyTaskService(transactionManager, studyTaskRepository, categoryRepository);
    }

    @AfterEach
    void tearDown() {
        entityManagerFactory.close();
    }

    @Test
    void persistsAndLoadsCategory() {
        categoryService.createCategory("Math");

        assertThat(categoryService.findAll())
                .extracting("name")
                .containsExactly("Math");
    }

    @Test
    void rollsBackTransactionWhenDuplicateCategoryIsRejected() {
        categoryService.createCategory("Math");

        assertThatThrownBy(() -> categoryService.createCategory("Math"))
                .isInstanceOf(DuplicateCategoryNameException.class);

        assertThat(categoryService.findAll())
                .extracting("name")
                .containsExactly("Math");
    }

    @Test
    void persistsAndLoadsTaskWithCategoryRelationship() {
        Long categoryId = categoryService.createCategory("Math").getId();

        studyTaskService.createTask(
                "Study algebra",
                "Revise equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                categoryId);

        assertThat(studyTaskService.findAll())
                .extracting(StudyTask::getTitle)
                .containsExactly("Study algebra");

        assertThat(studyTaskService.findAll().getFirst().getCategory().getName())
                .isEqualTo("Math");
    }

    @Test
    void updatesTaskStatusInsideTransaction() {
        Long categoryId = categoryService.createCategory("Math").getId();

        StudyTask task = studyTaskService.createTask(
                "Study algebra",
                "Revise equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                categoryId);

        studyTaskService.markCompleted(task.getId());

        assertThat(studyTaskService.findByStatus(TaskStatus.COMPLETED))
                .extracting(StudyTask::getTitle)
                .containsExactly("Study algebra");
    }

    private Map<String, String> persistenceProperties() {
        Map<String, String> properties = new HashMap<>();

        properties.put("jakarta.persistence.jdbc.url", POSTGRES.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", POSTGRES.getUsername());
        properties.put("jakarta.persistence.jdbc.password", POSTGRES.getPassword());
        properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        return properties;
    }
}
