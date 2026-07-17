package it.unifi.ast.studytaskmanager.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerView;
import it.unifi.ast.studytaskmanager.gui.TaskFormData;
import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.Priority;
import it.unifi.ast.studytaskmanager.service.CategoryService;
import it.unifi.ast.studytaskmanager.service.StudyTaskService;

@ExtendWith(MockitoExtension.class)
class StudyTaskManagerPresenterAdditionalCoverageTest {

    @Mock
    private StudyTaskManagerView view;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StudyTaskService studyTaskService;

    @Test
    void showsErrorWhenAddingCategoryFails() {
        when(view.askForCategoryName()).thenThrow(new RuntimeException("input error"));

        presenter().addCategory();

        verify(view).showError("Could not add category: input error");
    }

    @Test
    void doesNotUpdateCategoryWhenInputIsEmpty() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L));
        when(view.askForCategoryName()).thenReturn(Optional.of("   "));

        presenter().updateSelectedCategory();

        verify(categoryService, never()).updateCategory(anyLong(), anyString());
    }

    @Test
    void showsErrorWhenUpdatingCategoryFails() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L));
        when(view.askForCategoryName()).thenReturn(Optional.of("Physics"));
        when(categoryService.updateCategory(1L, "Physics")).thenThrow(new RuntimeException("duplicate"));

        presenter().updateSelectedCategory();

        verify(view).showError("Could not update category: duplicate");
    }

    @Test
    void showsErrorWhenDeletingCategoryFails() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L));
        when(view.confirmDeleteCategories(1)).thenReturn(true);
        doThrow(new RuntimeException("in use")).when(categoryService).deleteCategory(1L);

        presenter().deleteSelectedCategory();

        verify(view).showError("Could not delete category: in use");
    }

    @Test
    void showsErrorWhenAddingTaskFailsWhileLoadingCategories() {
        when(categoryService.findAll()).thenThrow(new RuntimeException("database error"));

        presenter().addTask();

        verify(view).showError("Could not add task: database error");
    }

    @Test
    void doesNotAddTaskWhenTitleIsEmpty() {
        List<Category> categories = List.of(new Category("Math"));
        TaskFormData taskFormData = new TaskFormData(
                "   ",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L);

        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories)).thenReturn(Optional.of(taskFormData));

        presenter().addTask();

        verify(studyTaskService, never()).createTask(
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void doesNotUpdateTaskWhenTitleIsEmpty() {
        List<Category> categories = List.of(new Category("Math"));
        TaskFormData taskFormData = new TaskFormData(
                "   ",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L);

        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories, "Update Task")).thenReturn(Optional.of(taskFormData));

        presenter().updateSelectedTask();

        verify(studyTaskService, never()).updateTask(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void showsErrorWhenUpdatingTaskFails() {
        List<Category> categories = List.of(new Category("Math"));
        TaskFormData taskFormData = new TaskFormData(
                "Study algebra",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L);

        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories, "Update Task")).thenReturn(Optional.of(taskFormData));
        when(studyTaskService.updateTask(
                1L,
                "Study algebra",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                1L))
                .thenThrow(new RuntimeException("missing task"));

        presenter().updateSelectedTask();

        verify(view).showError("Could not update task: missing task");
    }

    @Test
    void showsErrorWhenCompletingTaskFails() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(studyTaskService.markCompleted(1L)).thenThrow(new RuntimeException("missing task"));

        presenter().completeSelectedTask();

        verify(view).showError("Could not complete task: missing task");
    }

    @Test
    void showsErrorWhenMarkingTaskPendingFails() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(studyTaskService.markPending(1L)).thenThrow(new RuntimeException("missing task"));

        presenter().markSelectedTaskPending();

        verify(view).showError("Could not mark task as pending: missing task");
    }

    @Test
    void showsErrorWhenSearchingTasksFails() {
        when(view.taskSearchText()).thenReturn("algebra");
        when(studyTaskService.searchByTitle("algebra")).thenThrow(new RuntimeException("database error"));

        presenter().searchTasks();

        verify(view).showError("Could not search tasks: database error");
    }

    @Test
    void showsErrorWhenClearingTaskSearchFails() {
        doThrow(new RuntimeException("ui error")).when(view).clearTaskSearchText();

        presenter().clearTaskSearch();

        verify(view).showError("Could not clear task search: ui error");
    }

    @Test
    void showsErrorWhenDeletingTaskFails() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(view.confirmDeleteTasks(1)).thenReturn(true);
        doThrow(new RuntimeException("missing task")).when(studyTaskService).deleteTask(1L);

        presenter().deleteSelectedTask();

        verify(view).showError("Could not delete task: missing task");
    }

    private StudyTaskManagerPresenter presenter() {
        return new StudyTaskManagerPresenter(view, categoryService, studyTaskService);
    }
}
