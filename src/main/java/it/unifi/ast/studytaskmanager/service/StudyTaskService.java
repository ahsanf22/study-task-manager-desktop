package it.unifi.ast.studytaskmanager.service;

import java.time.LocalDate;
import java.util.List;

import it.unifi.ast.studytaskmanager.exception.ResourceNotFoundException;
import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.Priority;
import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.model.TaskStatus;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import it.unifi.ast.studytaskmanager.transaction.TransactionManager;

public class StudyTaskService {

    private final TransactionManager transactionManager;
    private final StudyTaskRepository studyTaskRepository;
    private final CategoryRepository categoryRepository;

    public StudyTaskService(
            TransactionManager transactionManager,
            StudyTaskRepository studyTaskRepository,
            CategoryRepository categoryRepository) {
        this.transactionManager = transactionManager;
        this.studyTaskRepository = studyTaskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<StudyTask> findAll() {
        return transactionManager.doInTransaction(studyTaskRepository::findAll);
    }

    public StudyTask findById(Long id) {
        return transactionManager.doInTransaction(() -> findExistingTask(id));
    }

    public List<StudyTask> findByCategory(Long categoryId) {
        return transactionManager.doInTransaction(() -> studyTaskRepository.findByCategoryId(categoryId));
    }

    public List<StudyTask> findByStatus(TaskStatus status) {
        return transactionManager.doInTransaction(() -> studyTaskRepository.findByStatus(status));
    }

    public List<StudyTask> searchByTitle(String title) {
        return transactionManager.doInTransaction(() -> studyTaskRepository.findByTitleContaining(title.trim()));
    }

    public StudyTask createTask(
            String title,
            String description,
            Priority priority,
            LocalDate deadline,
            Long categoryId) {
        return transactionManager.doInTransaction(() -> {
            Category category = findExistingCategory(categoryId);
            StudyTask task = new StudyTask(title, description, priority, deadline, category);

            return studyTaskRepository.save(task);
        });
    }

    public StudyTask updateTask(
            Long taskId,
            String title,
            String description,
            Priority priority,
            LocalDate deadline,
            Long categoryId) {
        return transactionManager.doInTransaction(() -> {
            StudyTask task = findExistingTask(taskId);
            Category category = findExistingCategory(categoryId);

            task.updateDetails(title, description, priority, deadline, category);

            return studyTaskRepository.save(task);
        });
    }

    public StudyTask markCompleted(Long taskId) {
        return transactionManager.doInTransaction(() -> {
            StudyTask task = findExistingTask(taskId);
            task.markCompleted();

            return studyTaskRepository.save(task);
        });
    }

    public StudyTask markPending(Long taskId) {
        return transactionManager.doInTransaction(() -> {
            StudyTask task = findExistingTask(taskId);
            task.markPending();

            return studyTaskRepository.save(task);
        });
    }

    public void deleteTask(Long taskId) {
        transactionManager.doInTransaction(() -> {
            findExistingTask(taskId);
            studyTaskRepository.deleteById(taskId);
        });
    }

    private StudyTask findExistingTask(Long taskId) {
        return studyTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private Category findExistingCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}
