package it.unifi.ast.studytaskmanager.gui;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.StudyTask;

public interface StudyTaskManagerView {

    void showCategories(List<Category> categories);

    void showTasks(List<StudyTask> tasks);

    void showError(String message);

    boolean confirmDeleteCategories(int categoryCount);

    boolean confirmDeleteTasks(int taskCount);

    Optional<String> askForCategoryName();

    Optional<TaskFormData> askForTaskDetails(List<Category> categories);

    Optional<TaskFormData> askForTaskDetails(List<Category> categories, String dialogTitle);

    Optional<Long> selectedCategoryId();

    Optional<Long> selectedTaskId();

    List<Long> selectedCategoryIds();

    List<Long> selectedTaskIds();

    String taskSearchText();

    void clearTaskSearchText();

    void setAddCategoryAction(Runnable action);

    void setDeleteCategoryAction(Runnable action);

    void setAddTaskAction(Runnable action);

    void setCompleteTaskAction(Runnable action);

    void setDeleteTaskAction(Runnable action);

    void setUpdateCategoryAction(Runnable action);

    void setUpdateTaskAction(Runnable action);

    void setPendingTaskAction(Runnable action);

    void setSearchTasksAction(Runnable action);

    void setClearTaskSearchAction(Runnable action);
}
