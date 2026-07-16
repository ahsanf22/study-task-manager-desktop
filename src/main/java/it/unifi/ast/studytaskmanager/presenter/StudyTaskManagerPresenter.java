package it.unifi.ast.studytaskmanager.presenter;

import java.util.List;

import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerView;
import it.unifi.ast.studytaskmanager.gui.TaskFormData;
import it.unifi.ast.studytaskmanager.model.Category;
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
        this.view.setAddTaskAction(this::addTask);
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

    public void addTask() {
        try {
            List<Category> categories = categoryService.findAll();

            if (categories.isEmpty()) {
                view.showError("Please create a category before adding a task.");
                return;
            }

            view.askForTaskDetails(categories)
                    .filter(task -> !task.title().trim().isEmpty())
                    .ifPresent(this::createTaskAndReload);
        } catch (RuntimeException exception) {
            view.showError("Could not add task: " + exception.getMessage());
        }
    }

    private void createCategoryAndReload(String categoryName) {
        categoryService.createCategory(categoryName);
        reloadData();
    }

    private void createTaskAndReload(TaskFormData task) {
        studyTaskService.createTask(
                task.title().trim(),
                task.description().trim(),
                task.priority(),
                task.deadline(),
                task.categoryId());

        reloadData();
    }

    private void reloadData() {
        view.showCategories(categoryService.findAll());
        view.showTasks(studyTaskService.findAll());
    }
}
