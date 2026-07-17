package it.unifi.ast.studytaskmanager.gui;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.Test;

import it.unifi.ast.studytaskmanager.model.Category;

class StudyTaskManagerPanelTest {

    @Test
    void createsMainTablesAndButtons() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        assertThat(panel.getCategoryTable().getName())
                .isEqualTo(StudyTaskManagerPanel.CATEGORY_TABLE_NAME);
        assertThat(panel.getTaskTable().getName())
                .isEqualTo(StudyTaskManagerPanel.TASK_TABLE_NAME);

        assertButton(panel.getAddCategoryButton(), "Add Category", StudyTaskManagerPanel.ADD_CATEGORY_BUTTON_NAME);
        assertButton(panel.getDeleteCategoryButton(), "Delete Category", StudyTaskManagerPanel.DELETE_CATEGORY_BUTTON_NAME);
        assertButton(panel.getAddTaskButton(), "Add Task", StudyTaskManagerPanel.ADD_TASK_BUTTON_NAME);
        assertButton(panel.getCompleteTaskButton(), "Complete Task", StudyTaskManagerPanel.COMPLETE_TASK_BUTTON_NAME);
        assertButton(panel.getDeleteTaskButton(), "Delete Task", StudyTaskManagerPanel.DELETE_TASK_BUTTON_NAME);
        assertButton(panel.getUpdateCategoryButton(), "Update Category", StudyTaskManagerPanel.UPDATE_CATEGORY_BUTTON_NAME);
        assertButton(panel.getUpdateTaskButton(), "Update Task", StudyTaskManagerPanel.UPDATE_TASK_BUTTON_NAME);
        assertButton(panel.getPendingTaskButton(), "Mark Pending", StudyTaskManagerPanel.PENDING_TASK_BUTTON_NAME);
        assertButton(panel.getSearchTasksButton(), "Search Tasks", StudyTaskManagerPanel.SEARCH_TASKS_BUTTON_NAME);
        assertButton(panel.getClearTaskSearchButton(), "Clear Search", StudyTaskManagerPanel.CLEAR_TASK_SEARCH_BUTTON_NAME);
        assertThat(panel.getTaskSearchField().getName()).isEqualTo(StudyTaskManagerPanel.TASK_SEARCH_FIELD_NAME);
    }

    @Test
    void createsEditableSelectionColumnAndNonEditableDataColumnsWithoutRowHighlight() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        JTable categoryTable = panel.getCategoryTable();
        JTable taskTable = panel.getTaskTable();

        assertThat(categoryTable.isCellEditable(0, 0)).isTrue();
        assertThat(categoryTable.isCellEditable(0, 1)).isFalse();
        assertThat(categoryTable.getRowSelectionAllowed()).isFalse();

        assertThat(taskTable.isCellEditable(0, 0)).isTrue();
        assertThat(taskTable.isCellEditable(0, 1)).isFalse();
        assertThat(taskTable.getRowSelectionAllowed()).isFalse();
    }

    @Test
    void createsExpectedTableColumns() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        assertThat(panel.getCategoryTable().getColumnCount()).isEqualTo(3);
        assertThat(panel.getTaskTable().getColumnCount()).isEqualTo(7);

        assertThat(panel.getCategoryTable().getColumnName(0)).isEqualTo("Select");
        assertThat(panel.getCategoryTable().getColumnName(1)).isEqualTo("ID");
        assertThat(panel.getCategoryTable().getColumnName(2)).isEqualTo("Name");

        assertThat(panel.getTaskTable().getColumnName(0)).isEqualTo("Select");
        assertThat(panel.getTaskTable().getColumnName(1)).isEqualTo("ID");
        assertThat(panel.getTaskTable().getColumnName(2)).isEqualTo("Title");
        assertThat(panel.getTaskTable().getColumnName(3)).isEqualTo("Category");
        assertThat(panel.getTaskTable().getColumnName(4)).isEqualTo("Priority");
        assertThat(panel.getTaskTable().getColumnName(5)).isEqualTo("Deadline");
        assertThat(panel.getTaskTable().getColumnName(6)).isEqualTo("Status");
    }

    @Test
    void displaysCategoriesInCategoryTable() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        SwingUtilities.invokeAndWait(() -> panel.showCategories(List.of(new Category("Math"))));

        assertThat(panel.getCategoryTable().getRowCount()).isEqualTo(1);
        assertThat(panel.getCategoryTable().getValueAt(0, 2)).isEqualTo("Math");
    }

    @Test
    void executesAddCategoryActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setAddCategoryAction(() -> actionExecuted.set(true));
            panel.getAddCategoryButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesDeleteCategoryActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setDeleteCategoryAction(() -> actionExecuted.set(true));
            panel.getDeleteCategoryButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesAddTaskActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setAddTaskAction(() -> actionExecuted.set(true));
            panel.getAddTaskButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesCompleteTaskActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setCompleteTaskAction(() -> actionExecuted.set(true));
            panel.getCompleteTaskButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesDeleteTaskActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setDeleteTaskAction(() -> actionExecuted.set(true));
            panel.getDeleteTaskButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }


    @Test
    void executesUpdateCategoryActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setUpdateCategoryAction(() -> actionExecuted.set(true));
            panel.getUpdateCategoryButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesUpdateTaskActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setUpdateTaskAction(() -> actionExecuted.set(true));
            panel.getUpdateTaskButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesPendingTaskActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setPendingTaskAction(() -> actionExecuted.set(true));
            panel.getPendingTaskButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesSearchTasksActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setSearchTasksAction(() -> actionExecuted.set(true));
            panel.getSearchTasksButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void executesClearTaskSearchActionWhenButtonIsClicked() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();
        AtomicBoolean actionExecuted = new AtomicBoolean(false);

        SwingUtilities.invokeAndWait(() -> {
            panel.setClearTaskSearchAction(() -> actionExecuted.set(true));
            panel.getClearTaskSearchButton().doClick();
        });

        assertThat(actionExecuted).isTrue();
    }

    @Test
    void returnsAndClearsTaskSearchText() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        SwingUtilities.invokeAndWait(() -> panel.getTaskSearchField().setText("algebra"));

        assertThat(panel.taskSearchText()).isEqualTo("algebra");

        SwingUtilities.invokeAndWait(panel::clearTaskSearchText);

        assertThat(panel.getTaskSearchField().getText()).isEmpty();
    }


    @Test
    void returnsCheckedCategoryIds() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        SwingUtilities.invokeAndWait(() -> {
            DefaultTableModel model = (DefaultTableModel) panel.getCategoryTable().getModel();
            model.addRow(new Object[] { Boolean.TRUE, 1L, "Math" });
            model.addRow(new Object[] { Boolean.FALSE, 2L, "Science" });
            model.addRow(new Object[] { Boolean.TRUE, 3L, "English" });
        });

        assertThat(panel.selectedCategoryIds()).containsExactly(1L, 3L);
    }

    @Test
    void returnsCheckedTaskIds() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        SwingUtilities.invokeAndWait(() -> {
            DefaultTableModel model = (DefaultTableModel) panel.getTaskTable().getModel();
            model.addRow(new Object[] { Boolean.TRUE, 1L, "Task 1", "Math", "HIGH", "2026-07-20", "PENDING" });
            model.addRow(new Object[] { Boolean.FALSE, 2L, "Task 2", "Math", "LOW", "2026-07-21", "PENDING" });
            model.addRow(new Object[] { Boolean.TRUE, 3L, "Task 3", "Math", "MEDIUM", "2026-07-22", "PENDING" });
        });

        assertThat(panel.selectedTaskIds()).containsExactly(1L, 3L);
    }

    @Test
    void returnsEmptySelectedCategoryIdWhenNoCategoryIsSelected() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        assertThat(panel.selectedCategoryId()).isEmpty();
    }

    @Test
    void returnsEmptySelectedTaskIdWhenNoTaskIsSelected() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        assertThat(panel.selectedTaskId()).isEmpty();
    }

    private StudyTaskManagerPanel createPanelOnEventDispatchThread()
            throws InvocationTargetException, InterruptedException {
        AtomicReference<StudyTaskManagerPanel> panelReference = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> panelReference.set(new StudyTaskManagerPanel()));

        return panelReference.get();
    }

    private void assertButton(JButton button, String text, String name) {
        assertThat(button.getText()).isEqualTo(text);
        assertThat(button.getName()).isEqualTo(name);
    }
}
