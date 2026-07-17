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
        this.view.setUpdateCategoryAction(this::updateSelectedCategory);
        this.view.setUpdateTaskAction(this::updateSelectedTask);
        this.view.setPendingTaskAction(this::markSelectedTaskPending);
        this.view.setSearchTasksAction(this::searchTasks);
        this.view.setClearTaskSearchAction(this::clearTaskSearch);
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

    public void updateSelectedCategory() {
        try {
            List<Long> categoryIds = view.selectedCategoryIds();

            if (categoryIds.size() != 1) {
                view.showError("Please select exactly one category to update.");
                return;
            }

            view.askForCategoryName()
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .ifPresent(name -> updateCategoryAndReload(categoryIds.get(0), name));
        } catch (RuntimeException exception) {
            view.showError("Could not update category: " + exception.getMessage());
        }
    }

    public void deleteSelectedCategory() {
        try {
            List<Long> categoryIds = view.selectedCategoryIds();

            if (categoryIds.isEmpty()) {
                view.showError("Please select at least one category to delete.");
                return;
            }

            if (view.confirmDeleteCategories(categoryIds.size())) {
                deleteCategoriesAndReload(categoryIds);
            }
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

    public void updateSelectedTask() {
        try {
            List<Long> taskIds = view.selectedTaskIds();

            if (taskIds.size() != 1) {
                view.showError("Please select exactly one task to update.");
                return;
            }

            List<Category> categories = categoryService.findAll();

            if (categories.isEmpty()) {
                view.showError("Please create a category before updating a task.");
                return;
            }

            view.askForTaskDetails(categories, "Update Task")
                    .filter(task -> !task.title().trim().isEmpty())
                    .ifPresent(task -> updateTaskAndReload(taskIds.get(0), task));
        } catch (RuntimeException exception) {
            view.showError("Could not update task: " + exception.getMessage());
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

    public void markSelectedTaskPending() {
        try {
            List<Long> taskIds = view.selectedTaskIds();

            if (taskIds.isEmpty()) {
                view.showError("Please select at least one task to mark as pending.");
                return;
            }

            markTasksPendingAndReload(taskIds);
        } catch (RuntimeException exception) {
            view.showError("Could not mark task as pending: " + exception.getMessage());
        }
    }

    public void searchTasks() {
        try {
            String title = view.taskSearchText().trim();

            if (title.isEmpty()) {
                reloadData();
                return;
            }

            view.showTasks(studyTaskService.searchByTitle(title));
        } catch (RuntimeException exception) {
            view.showError("Could not search tasks: " + exception.getMessage());
        }
    }

    public void clearTaskSearch() {
        try {
            view.clearTaskSearchText();
            reloadData();
        } catch (RuntimeException exception) {
            view.showError("Could not clear task search: " + exception.getMessage());
        }
    }

    public void deleteSelectedTask() {
        try {
            List<Long> taskIds = view.selectedTaskIds();

            if (taskIds.isEmpty()) {
                view.showError("Please select at least one task to delete.");
                return;
            }

            if (view.confirmDeleteTasks(taskIds.size())) {
                deleteTasksAndReload(taskIds);
            }
        } catch (RuntimeException exception) {
            view.showError("Could not delete task: " + exception.getMessage());
        }
    }

    private void createCategoryAndReload(String categoryName) {
        categoryService.createCategory(categoryName);
        reloadData();
    }

    private void updateCategoryAndReload(Long categoryId, String categoryName) {
        categoryService.updateCategory(categoryId, categoryName);
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

    private void updateTaskAndReload(Long taskId, TaskFormData task) {
        studyTaskService.updateTask(
                taskId,
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

    private void markTasksPendingAndReload(List<Long> taskIds) {
        for (Long taskId : taskIds) {
            studyTaskService.markPending(taskId);
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
