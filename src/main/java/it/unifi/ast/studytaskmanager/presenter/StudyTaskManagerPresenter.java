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

        this.view.setAddCategoryAction(this::addCategory);
    }

    public void loadInitialData() {
        try {
            reloadData();
        } catch (RuntimeException exception) {
            view.showError("Could not load data: " + exception.getMessage());
        }
    }

    public void addCategory() {
        try {
            view.askForCategoryName()
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .ifPresent(this::createCategoryAndReload);
        } catch (RuntimeException exception) {
            view.showError("Could not add category: " + exception.getMessage());
        }
    }

    private void createCategoryAndReload(String categoryName) {
        categoryService.createCategory(categoryName);
        reloadData();
    }

    private void reloadData() {
        view.showCategories(categoryService.findAll());
        view.showTasks(studyTaskService.findAll());
    }
}
