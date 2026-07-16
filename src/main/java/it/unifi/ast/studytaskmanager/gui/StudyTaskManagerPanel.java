package it.unifi.ast.studytaskmanager.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class StudyTaskManagerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final String CATEGORY_TABLE_NAME = "categoryTable";
    public static final String TASK_TABLE_NAME = "taskTable";
    public static final String ADD_CATEGORY_BUTTON_NAME = "addCategoryButton";
    public static final String ADD_TASK_BUTTON_NAME = "addTaskButton";
    public static final String COMPLETE_TASK_BUTTON_NAME = "completeTaskButton";
    public static final String DELETE_TASK_BUTTON_NAME = "deleteTaskButton";

    private final JTable categoryTable;
    private final JTable taskTable;
    private final JButton addCategoryButton;
    private final JButton addTaskButton;
    private final JButton completeTaskButton;
    private final JButton deleteTaskButton;

    public StudyTaskManagerPanel() {
        super(new BorderLayout(10, 10));

        categoryTable = createCategoryTable();
        taskTable = createTaskTable();

        addCategoryButton = createButton("Add Category", ADD_CATEGORY_BUTTON_NAME);
        addTaskButton = createButton("Add Task", ADD_TASK_BUTTON_NAME);
        completeTaskButton = createButton("Complete Task", COMPLETE_TASK_BUTTON_NAME);
        deleteTaskButton = createButton("Delete Task", DELETE_TASK_BUTTON_NAME);

        add(createHeader(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    public JTable getCategoryTable() {
        return categoryTable;
    }

    public JTable getTaskTable() {
        return taskTable;
    }

    public JButton getAddCategoryButton() {
        return addCategoryButton;
    }

    public JButton getAddTaskButton() {
        return addTaskButton;
    }

    public JButton getCompleteTaskButton() {
        return completeTaskButton;
    }

    public JButton getDeleteTaskButton() {
        return deleteTaskButton;
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Study Task Manager Desktop");
        header.setName("headerLabel");

        return header;
    }

    private JSplitPane createTablesPanel() {
        JScrollPane categoriesScrollPane = new JScrollPane(categoryTable);
        JScrollPane tasksScrollPane = new JScrollPane(taskTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoriesScrollPane, tasksScrollPane);
        splitPane.setResizeWeight(0.35);

        return splitPane;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        buttonsPanel.add(addCategoryButton);
        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(completeTaskButton);
        buttonsPanel.add(deleteTaskButton);

        return buttonsPanel;
    }

    private JTable createCategoryTable() {
        NonEditableTableModel model = new NonEditableTableModel("ID", "Name");

        JTable table = new JTable(model);
        table.setName(CATEGORY_TABLE_NAME);

        return table;
    }

    private JTable createTaskTable() {
        NonEditableTableModel model = new NonEditableTableModel(
                "ID",
                "Title",
                "Category",
                "Priority",
                "Deadline",
                "Status");

        JTable table = new JTable(model);
        table.setName(TASK_TABLE_NAME);

        return table;
    }

    private JButton createButton(String text, String name) {
        JButton button = new JButton(text);
        button.setName(name);

        return button;
    }

    private static final class NonEditableTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        NonEditableTableModel(String... columnNames) {
            super(columnNames, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
