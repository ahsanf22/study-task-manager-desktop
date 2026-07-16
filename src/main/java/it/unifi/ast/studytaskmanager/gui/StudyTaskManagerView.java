package it.unifi.ast.studytaskmanager.gui;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.StudyTask;

public interface StudyTaskManagerView {

    void showCategories(List<Category> categories);

    void showTasks(List<StudyTask> tasks);

    void showError(String message);

    Optional<String> askForCategoryName();

    Optional<TaskFormData> askForTaskDetails(List<Category> categories);

    Optional<Long> selectedTaskId();

    void setAddCategoryAction(Runnable action);

    void setAddTaskAction(Runnable action);

    void setCompleteTaskAction(Runnable action);
}
