package it.unifi.ast.studytaskmanager.presenter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerView;
import it.unifi.ast.studytaskmanager.model.Category;
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
}
