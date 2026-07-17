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
        this.view.setDeleteCategoryAction(this::deleteSelectedCategory);
        this.view.setAddTaskAction(this::addTask);
        this.view.setCompleteTaskAction(this::completeSelectedTask);
        this.view.setDeleteTaskAction(this::deleteSelectedTask);
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

    public void deleteSelectedCategory() {
        try {
            List<Long> categoryIds = view.selectedCategoryIds();

            if (categoryIds.isEmpty()) {
                view.showError("Please select at least one category to delete.");
                return;
            }

            deleteCategoriesAndReload(categoryIds);
        } catch (RuntimeException exception) {
            view.showError("Could not delete category: " + exception.getMessage());
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

    public void completeSelectedTask() {
        try {
            List<Long> taskIds = view.selectedTaskIds();

            if (taskIds.isEmpty()) {
                view.showError("Please select at least one task to complete.");
                return;
            }

            markTasksCompletedAndReload(taskIds);
        } catch (RuntimeException exception) {
            view.showError("Could not complete task: " + exception.getMessage());
        }
    }

    public void deleteSelectedTask() {
        try {
            List<Long> taskIds = view.selectedTaskIds();

            if (taskIds.isEmpty()) {
                view.showError("Please select at least one task to delete.");
                return;
            }

            deleteTasksAndReload(taskIds);
        } catch (RuntimeException exception) {
            view.showError("Could not delete task: " + exception.getMessage());
        }
    }

    private void createCategoryAndReload(String categoryName) {
        categoryService.createCategory(categoryName);
        reloadData();
    }

    private void deleteCategoriesAndReload(List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            categoryService.deleteCategory(categoryId);
        }

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

    private void markTasksCompletedAndReload(List<Long> taskIds) {
        for (Long taskId : taskIds) {
            studyTaskService.markCompleted(taskId);
        }

        reloadData();
    }

    private void deleteTasksAndReload(List<Long> taskIds) {
        for (Long taskId : taskIds) {
            studyTaskService.deleteTask(taskId);
        }

        reloadData();
    }

    private void reloadData() {
        view.showCategories(categoryService.findAll());
        view.showTasks(studyTaskService.findAll());
    }
}
