package it.unifi.ast.studytaskmanager.presenter;

import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerView;
import it.unifi.ast.studytaskmanager.service.CategoryService;
import it.unifi.ast.studytaskmanager.service.StudyTaskService;

public class StudyTaskManagerPresenter {

    private final StudyTaskManagerView view;
    private final CategoryService categoryService;
    private final StudyTaskService studyTaskService;

    public StudyTaskManagerPresenter(
            StudyTaskManagerView view,
            CategoryService categoryService,
            StudyTaskService studyTaskService) {
        this.view = view;
        this.categoryService = categoryService;
        this.studyTaskService = studyTaskService;
    }

    public void loadInitialData() {
        try {
            view.showCategories(categoryService.findAll());
            view.showTasks(studyTaskService.findAll());
        } catch (RuntimeException exception) {
            view.showError("Could not load data: " + exception.getMessage());
        }
    }
}
