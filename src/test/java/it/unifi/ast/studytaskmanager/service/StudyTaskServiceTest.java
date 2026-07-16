package it.unifi.ast.studytaskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import it.unifi.ast.studytaskmanager.exception.ResourceNotFoundException;
import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.Priority;
import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.model.TaskStatus;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import it.unifi.ast.studytaskmanager.transaction.ImmediateTransactionManager;

class StudyTaskServiceTest {

    private StudyTaskRepository studyTaskRepository;
    private CategoryRepository categoryRepository;
    private StudyTaskService studyTaskService;

    @BeforeEach
    void setUp() {
        studyTaskRepository = Mockito.mock(StudyTaskRepository.class);
        categoryRepository = Mockito.mock(CategoryRepository.class);
        studyTaskService = new StudyTaskService(
                new ImmediateTransactionManager(),
                studyTaskRepository,
                categoryRepository);
    }

    @Test
    void findsAllTasks() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findAll()).thenReturn(List.of(task));

        assertThat(studyTaskService.findAll()).containsExactly(task);
    }

    @Test
    void findsTaskById() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThat(studyTaskService.findById(1L)).isEqualTo(task);
    }

    @Test
    void throwsWhenTaskIsNotFoundById() {
        when(studyTaskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyTaskService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 1");
    }

    @Test
    void findsTasksByCategory() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findByCategoryId(1L)).thenReturn(List.of(task));

        assertThat(studyTaskService.findByCategory(1L)).containsExactly(task);
    }

    @Test
    void findsTasksByStatus() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(task));

        assertThat(studyTaskService.findByStatus(TaskStatus.PENDING)).containsExactly(task);
    }

    @Test
    void searchesTasksByTitle() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findByTitleContaining("algebra")).thenReturn(List.of(task));

        assertThat(studyTaskService.searchByTitle("  algebra  ")).containsExactly(task);
    }

    @Test
    void createsTaskWhenCategoryExists() {
        Category category = category();
        StudyTask savedTask = task("Study algebra");
        savedTask.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(studyTaskRepository.save(argThat(task -> "Study algebra".equals(task.getTitle())))).thenReturn(savedTask);

        StudyTask createdTask = studyTaskService.createTask(
                "Study algebra",
                "Revise equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L);

        assertThat(createdTask).isEqualTo(savedTask);
        verify(studyTaskRepository).save(argThat(task -> "Study algebra".equals(task.getTitle())));
    }

    @Test
    void rejectsCreatingTaskWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyTaskService.createTask(
                "Study algebra",
                "Revise equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");

        verify(studyTaskRepository, never()).save(argThat(task -> true));
    }

    @Test
    void updatesTaskWhenTaskAndCategoryExist() {
        StudyTask task = task("Study algebra");
        Category category = category();

        when(studyTaskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(studyTaskRepository.save(task)).thenReturn(task);

        StudyTask updatedTask = studyTaskService.updateTask(
                1L,
                "Study geometry",
                "Revise triangles",
                Priority.MEDIUM,
                LocalDate.of(2026, 7, 21),
                2L);

        assertThat(updatedTask.getTitle()).isEqualTo("Study geometry");
        assertThat(updatedTask.getDescription()).isEqualTo("Revise triangles");
        assertThat(updatedTask.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(updatedTask.getDeadline()).isEqualTo(LocalDate.of(2026, 7, 21));
        verify(studyTaskRepository).save(task);
    }

    @Test
    void rejectsUpdatingMissingTask() {
        when(studyTaskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyTaskService.updateTask(
                1L,
                "Study geometry",
                "Revise triangles",
                Priority.MEDIUM,
                LocalDate.of(2026, 7, 21),
                2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 1");

        verify(studyTaskRepository, never()).save(argThat(task -> true));
    }

    @Test
    void marksTaskCompleted() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(studyTaskRepository.save(task)).thenReturn(task);

        StudyTask completedTask = studyTaskService.markCompleted(1L);

        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(studyTaskRepository).save(task);
    }

    @Test
    void marksTaskPending() {
        StudyTask task = task("Study algebra");
        task.markCompleted();

        when(studyTaskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(studyTaskRepository.save(task)).thenReturn(task);

        StudyTask pendingTask = studyTaskService.markPending(1L);

        assertThat(pendingTask.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(studyTaskRepository).save(task);
    }

    @Test
    void deletesTaskWhenItExists() {
        StudyTask task = task("Study algebra");

        when(studyTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        studyTaskService.deleteTask(1L);

        verify(studyTaskRepository).deleteById(1L);
    }

    @Test
    void rejectsDeletingMissingTask() {
        when(studyTaskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyTaskService.deleteTask(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 1");

        verify(studyTaskRepository, never()).deleteById(1L);
    }

    private StudyTask task(String title) {
        return new StudyTask(
                title,
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                category());
    }

    private Category category() {
        Category category = new Category("Math");
        category.setId(1L);

        return category;
    }
}
