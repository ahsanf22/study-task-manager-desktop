package it.unifi.ast.studytaskmanager.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
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
import it.unifi.ast.studytaskmanager.model.StudyTask;
import it.unifi.ast.studytaskmanager.service.CategoryService;
import it.unifi.ast.studytaskmanager.service.StudyTaskService;

@ExtendWith(MockitoExtension.class)
class StudyTaskManagerPresenterTest {

    @Mock
    private StudyTaskManagerView view;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StudyTaskService studyTaskService;

    @Test
    void loadsInitialDataIntoView() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.loadInitialData();

        verify(categoryService).findAll();
        verify(studyTaskService).findAll();
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenInitialDataCannotBeLoaded() {
        when(categoryService.findAll()).thenThrow(new RuntimeException("database unavailable"));

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.loadInitialData();

        verify(view).showError("Could not load data: database unavailable");
        verifyNoInteractions(studyTaskService);
    }

    @Test
    void addsCategoryAndReloadsData() {
        List<Category> categories = List.of(new Category("Math"));

        when(view.askForCategoryName()).thenReturn(Optional.of(" Math "));
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addCategory();

        verify(categoryService).createCategory("Math");
        verify(view).showCategories(categories);
        verify(view).showTasks(List.of());
    }

    @Test
    void doesNotAddCategoryWhenInputIsEmpty() {
        when(view.askForCategoryName()).thenReturn(Optional.of("   "));

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addCategory();

        verify(categoryService, never()).createCategory(anyString());
    }

    @Test
    void doesNotAddCategoryWhenDialogIsCancelled() {
        when(view.askForCategoryName()).thenReturn(Optional.empty());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addCategory();

        verify(categoryService, never()).createCategory(anyString());
    }

    @Test
    void deletesSelectedCategoriesAndReloadsData() {
        List<Category> categories = List.of();
        List<StudyTask> tasks = List.of();

        when(view.selectedCategoryIds()).thenReturn(List.of(1L, 2L));
        when(view.confirmDeleteCategories(2)).thenReturn(true);
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedCategory();

        verify(categoryService).deleteCategory(1L);
        verify(categoryService).deleteCategory(2L);
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenDeletingCategoryWithoutSelection() {
        when(view.selectedCategoryIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedCategory();

        verify(view).showError("Please select at least one category to delete.");
        verify(categoryService, never()).deleteCategory(anyLong());
    }

    @Test
    void addsTaskAndReloadsData() {
        Category category = new Category("Math");
        List<Category> categories = List.of(category);
        TaskFormData taskFormData = new TaskFormData(
                " Read Chapter 1 ",
                " RRTC preparation ",
                Priority.HIGH,
                LocalDate.of(2026, Month.JULY, 20),
                1L);

        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories)).thenReturn(Optional.of(taskFormData));
        when(studyTaskService.findAll()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addTask();

        verify(studyTaskService).createTask(
                "Read Chapter 1",
                "RRTC preparation",
                Priority.HIGH,
                LocalDate.of(2026, Month.JULY, 20),
                1L);
        verify(categoryService, times(2)).findAll();
        verify(view).showCategories(categories);
        verify(view).showTasks(List.of());
    }

    @Test
    void doesNotAddTaskWhenThereAreNoCategories() {
        when(categoryService.findAll()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addTask();

        verify(view).showError("Please create a category before adding a task.");
        verifyNoInteractions(studyTaskService);
    }

    @Test
    void doesNotAddTaskWhenDialogIsCancelled() {
        List<Category> categories = List.of(new Category("Math"));

        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories)).thenReturn(Optional.empty());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.addTask();

        verify(studyTaskService, never()).createTask(
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void completesSelectedTasksAndReloadsData() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(view.selectedTaskIds()).thenReturn(List.of(1L, 2L));
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.completeSelectedTask();

        verify(studyTaskService).markCompleted(1L);
        verify(studyTaskService).markCompleted(2L);
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenCompletingTaskWithoutSelection() {
        when(view.selectedTaskIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.completeSelectedTask();

        verify(view).showError("Please select at least one task to complete.");
        verify(studyTaskService, never()).markCompleted(anyLong());
    }

    @Test
    void deletesSelectedTasksAndReloadsData() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(view.selectedTaskIds()).thenReturn(List.of(1L, 2L));
        when(view.confirmDeleteTasks(2)).thenReturn(true);
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedTask();

        verify(studyTaskService).deleteTask(1L);
        verify(studyTaskService).deleteTask(2L);
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenDeletingTaskWithoutSelection() {
        when(view.selectedTaskIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedTask();

        verify(view).showError("Please select at least one task to delete.");
        verify(studyTaskService, never()).deleteTask(anyLong());
    }

    @Test
    void doesNotDeleteCategoriesWhenConfirmationIsRejected() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L, 2L));
        when(view.confirmDeleteCategories(2)).thenReturn(false);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedCategory();

        verify(categoryService, never()).deleteCategory(anyLong());
    }

    @Test
    void doesNotDeleteTasksWhenConfirmationIsRejected() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L, 2L));
        when(view.confirmDeleteTasks(2)).thenReturn(false);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.deleteSelectedTask();

        verify(studyTaskService, never()).deleteTask(anyLong());
    }

    @Test
    void updatesSelectedCategoryAndReloadsData() {
        List<Category> categories = List.of(new Category("Physics"));
        List<StudyTask> tasks = List.of();

        when(view.selectedCategoryIds()).thenReturn(List.of(1L));
        when(view.askForCategoryName()).thenReturn(Optional.of(" Physics "));
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedCategory();

        verify(categoryService).updateCategory(1L, "Physics");
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenUpdatingCategoryWithoutSelection() {
        when(view.selectedCategoryIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedCategory();

        verify(view).showError("Please select exactly one category to update.");
        verify(categoryService, never()).updateCategory(anyLong(), anyString());
    }

    @Test
    void showsErrorWhenUpdatingMoreThanOneCategory() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L, 2L));

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedCategory();

        verify(view).showError("Please select exactly one category to update.");
        verify(categoryService, never()).updateCategory(anyLong(), anyString());
    }

    @Test
    void doesNotUpdateCategoryWhenDialogIsCancelled() {
        when(view.selectedCategoryIds()).thenReturn(List.of(1L));
        when(view.askForCategoryName()).thenReturn(Optional.empty());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedCategory();

        verify(categoryService, never()).updateCategory(anyLong(), anyString());
    }

    @Test
    void updatesSelectedTaskAndReloadsData() {
        Category category = new Category("Math");
        List<Category> categories = List.of(category);
        List<StudyTask> tasks = List.of();
        TaskFormData taskFormData = new TaskFormData(
                " Updated Task ",
                " Updated description ",
                Priority.MEDIUM,
                LocalDate.of(2026, Month.JULY, 21),
                1L);

        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories, "Update Task")).thenReturn(Optional.of(taskFormData));
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedTask();

        verify(studyTaskService).updateTask(
                1L,
                "Updated Task",
                "Updated description",
                Priority.MEDIUM,
                LocalDate.of(2026, Month.JULY, 21),
                1L);
        verify(categoryService, times(2)).findAll();
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenUpdatingTaskWithoutSelection() {
        when(view.selectedTaskIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedTask();

        verify(view).showError("Please select exactly one task to update.");
        verify(studyTaskService, never()).updateTask(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void showsErrorWhenUpdatingMoreThanOneTask() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L, 2L));

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedTask();

        verify(view).showError("Please select exactly one task to update.");
        verify(studyTaskService, never()).updateTask(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void doesNotUpdateTaskWhenDialogIsCancelled() {
        List<Category> categories = List.of(new Category("Math"));

        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(categoryService.findAll()).thenReturn(categories);
        when(view.askForTaskDetails(categories, "Update Task")).thenReturn(Optional.empty());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedTask();

        verify(studyTaskService, never()).updateTask(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void showsErrorWhenUpdatingTaskWithoutCategories() {
        when(view.selectedTaskIds()).thenReturn(List.of(1L));
        when(categoryService.findAll()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.updateSelectedTask();

        verify(view).showError("Please create a category before updating a task.");
        verify(studyTaskService, never()).updateTask(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyLong());
    }

    @Test
    void marksSelectedTasksPendingAndReloadsData() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(view.selectedTaskIds()).thenReturn(List.of(1L, 2L));
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.markSelectedTaskPending();

        verify(studyTaskService).markPending(1L);
        verify(studyTaskService).markPending(2L);
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void showsErrorWhenMarkingPendingWithoutSelection() {
        when(view.selectedTaskIds()).thenReturn(List.of());

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.markSelectedTaskPending();

        verify(view).showError("Please select at least one task to mark as pending.");
        verify(studyTaskService, never()).markPending(anyLong());
    }

    @Test
    void searchesTasksByTitle() {
        StudyTask task = new StudyTask(
                "Study algebra",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, Month.JULY, 20),
                new Category("Math"));

        when(view.taskSearchText()).thenReturn(" algebra ");
        when(studyTaskService.searchByTitle("algebra")).thenReturn(List.of(task));

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.searchTasks();

        verify(studyTaskService).searchByTitle("algebra");
        verify(view).showTasks(List.of(task));
    }

    @Test
    void reloadsDataWhenSearchTextIsBlank() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(view.taskSearchText()).thenReturn("   ");
        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.searchTasks();

        verify(studyTaskService, never()).searchByTitle(anyString());
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }

    @Test
    void clearsTaskSearchAndReloadsData() {
        List<Category> categories = List.of(new Category("Math"));
        List<StudyTask> tasks = List.of();

        when(categoryService.findAll()).thenReturn(categories);
        when(studyTaskService.findAll()).thenReturn(tasks);

        StudyTaskManagerPresenter presenter =
                new StudyTaskManagerPresenter(view, categoryService, studyTaskService);

        presenter.clearTaskSearch();

        verify(view).clearTaskSearchText();
        verify(view).showCategories(categories);
        verify(view).showTasks(tasks);
    }


}

