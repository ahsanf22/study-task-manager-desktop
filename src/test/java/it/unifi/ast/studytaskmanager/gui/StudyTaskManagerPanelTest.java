package it.unifi.ast.studytaskmanager.gui;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

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
        assertButton(panel.getAddTaskButton(), "Add Task", StudyTaskManagerPanel.ADD_TASK_BUTTON_NAME);
        assertButton(panel.getCompleteTaskButton(), "Complete Task", StudyTaskManagerPanel.COMPLETE_TASK_BUTTON_NAME);
        assertButton(panel.getDeleteTaskButton(), "Delete Task", StudyTaskManagerPanel.DELETE_TASK_BUTTON_NAME);
    }

    @Test
    void createsNonEditableTables() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        JTable categoryTable = panel.getCategoryTable();
        JTable taskTable = panel.getTaskTable();

        assertThat(categoryTable.isCellEditable(0, 0)).isFalse();
        assertThat(taskTable.isCellEditable(0, 0)).isFalse();
    }

    @Test
    void createsExpectedTableColumns() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        assertThat(panel.getCategoryTable().getColumnCount()).isEqualTo(2);
        assertThat(panel.getTaskTable().getColumnCount()).isEqualTo(6);

        assertThat(panel.getCategoryTable().getColumnName(0)).isEqualTo("ID");
        assertThat(panel.getCategoryTable().getColumnName(1)).isEqualTo("Name");

        assertThat(panel.getTaskTable().getColumnName(0)).isEqualTo("ID");
        assertThat(panel.getTaskTable().getColumnName(1)).isEqualTo("Title");
        assertThat(panel.getTaskTable().getColumnName(2)).isEqualTo("Category");
        assertThat(panel.getTaskTable().getColumnName(3)).isEqualTo("Priority");
        assertThat(panel.getTaskTable().getColumnName(4)).isEqualTo("Deadline");
        assertThat(panel.getTaskTable().getColumnName(5)).isEqualTo("Status");
    }

    @Test
    void displaysCategoriesInCategoryTable() throws Exception {
        StudyTaskManagerPanel panel = createPanelOnEventDispatchThread();

        SwingUtilities.invokeAndWait(() -> panel.showCategories(List.of(new Category("Math"))));

        assertThat(panel.getCategoryTable().getRowCount()).isEqualTo(1);
        assertThat(panel.getCategoryTable().getValueAt(0, 1)).isEqualTo("Math");
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
