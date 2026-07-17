package it.unifi.ast.studytaskmanager.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.model.Priority;
import it.unifi.ast.studytaskmanager.model.StudyTask;

public class StudyTaskManagerPanel extends JPanel implements StudyTaskManagerView {

    private static final long serialVersionUID = 1L;

    public static final String CATEGORY_TABLE_NAME = "categoryTable";
    public static final String TASK_TABLE_NAME = "taskTable";
    public static final String ADD_CATEGORY_BUTTON_NAME = "addCategoryButton";
    public static final String DELETE_CATEGORY_BUTTON_NAME = "deleteCategoryButton";
    public static final String ADD_TASK_BUTTON_NAME = "addTaskButton";
    public static final String COMPLETE_TASK_BUTTON_NAME = "completeTaskButton";
    public static final String DELETE_TASK_BUTTON_NAME = "deleteTaskButton";
    public static final String UPDATE_CATEGORY_BUTTON_NAME = "updateCategoryButton";
    public static final String UPDATE_TASK_BUTTON_NAME = "updateTaskButton";
    public static final String PENDING_TASK_BUTTON_NAME = "pendingTaskButton";
    public static final String TASK_SEARCH_FIELD_NAME = "taskSearchField";
    public static final String SEARCH_TASKS_BUTTON_NAME = "searchTasksButton";
    public static final String CLEAR_TASK_SEARCH_BUTTON_NAME = "clearTaskSearchButton";

    private final JTable categoryTable;
    private final JTable taskTable;
    private final JButton addCategoryButton;
    private final JButton deleteCategoryButton;
    private final JButton addTaskButton;
    private final JButton completeTaskButton;
    private final JButton deleteTaskButton;
    private final JButton updateCategoryButton;
    private final JButton updateTaskButton;
    private final JButton pendingTaskButton;
    private final JTextField taskSearchField;
    private final JButton searchTasksButton;
    private final JButton clearTaskSearchButton;

    public StudyTaskManagerPanel() {
        super(new BorderLayout(10, 10));

        categoryTable = createCategoryTable();
        taskTable = createTaskTable();

        addCategoryButton = createButton("Add Category", ADD_CATEGORY_BUTTON_NAME);
        deleteCategoryButton = createButton("Delete Category", DELETE_CATEGORY_BUTTON_NAME);
        addTaskButton = createButton("Add Task", ADD_TASK_BUTTON_NAME);
        completeTaskButton = createButton("Complete Task", COMPLETE_TASK_BUTTON_NAME);
        deleteTaskButton = createButton("Delete Task", DELETE_TASK_BUTTON_NAME);
        updateCategoryButton = createButton("Update Category", UPDATE_CATEGORY_BUTTON_NAME);
        updateTaskButton = createButton("Update Task", UPDATE_TASK_BUTTON_NAME);
        pendingTaskButton = createButton("Mark Pending", PENDING_TASK_BUTTON_NAME);
        taskSearchField = new JTextField();
        taskSearchField.setName(TASK_SEARCH_FIELD_NAME);
        searchTasksButton = createButton("Search Tasks", SEARCH_TASKS_BUTTON_NAME);
        clearTaskSearchButton = createButton("Clear Search", CLEAR_TASK_SEARCH_BUTTON_NAME);

        add(createHeader(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void showCategories(List<Category> categories) {
        DefaultTableModel model = tableModel(categoryTable);
        model.setRowCount(0);

        for (Category category : categories) {
            model.addRow(new Object[] {
                    Boolean.FALSE,
                    category.getId(),
                    category.getName()
            });
        }
    }

    @Override
    public void showTasks(List<StudyTask> tasks) {
        DefaultTableModel model = tableModel(taskTable);
        model.setRowCount(0);

        for (StudyTask task : tasks) {
            model.addRow(new Object[] {
                    Boolean.FALSE,
                    task.getId(),
                    task.getTitle(),
                    categoryName(task),
                    task.getPriority(),
                    task.getDeadline(),
                    task.getStatus()
            });
        }
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Study Task Manager",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public boolean confirmDeleteCategories(int categoryCount) {
        return confirmDeletion(categoryCount, "category", "categories");
    }

    @Override
    public boolean confirmDeleteTasks(int taskCount) {
        return confirmDeletion(taskCount, "task", "tasks");
    }

    @Override
    public Optional<String> askForCategoryName() {
        String categoryName = JOptionPane.showInputDialog(
                this,
                "Category name:",
                "Add Category",
                JOptionPane.PLAIN_MESSAGE);

        return Optional.ofNullable(categoryName);
    }

    @Override
    public Optional<TaskFormData> askForTaskDetails(List<Category> categories) {
        return askForTaskDetails(categories, "Add Task");
    }

    @Override
    public Optional<TaskFormData> askForTaskDetails(List<Category> categories, String dialogTitle) {
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField deadlineField = new JTextField(LocalDate.now(Clock.systemDefaultZone()).plusDays(7).toString());

        JComboBox<Priority> priorityComboBox = new JComboBox<>(Priority.values());
        JComboBox<CategoryItem> categoryComboBox = new JComboBox<>(
                categories.stream()
                        .map(category -> new CategoryItem(category.getId(), category.getName()))
                        .toArray(CategoryItem[]::new));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Description:"));
        form.add(descriptionField);
        form.add(new JLabel("Priority:"));
        form.add(priorityComboBox);
        form.add(new JLabel("Deadline yyyy-MM-dd:"));
        form.add(deadlineField);
        form.add(new JLabel("Category:"));
        form.add(categoryComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                dialogTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return Optional.empty();
        }

        CategoryItem selectedCategory = (CategoryItem) categoryComboBox.getSelectedItem();

        return Optional.of(new TaskFormData(
                titleField.getText(),
                descriptionField.getText(),
                (Priority) priorityComboBox.getSelectedItem(),
                LocalDate.parse(deadlineField.getText().trim()),
                selectedCategory.id()));
    }

    @Override
    public Optional<Long> selectedCategoryId() {
        return selectedRowId(categoryTable, 1);
    }

    @Override
    public Optional<Long> selectedTaskId() {
        return selectedRowId(taskTable, 1);
    }

    @Override
    public List<Long> selectedCategoryIds() {
        return selectedIds(categoryTable, 1);
    }

    @Override
    public List<Long> selectedTaskIds() {
        return selectedIds(taskTable, 1);
    }

    @Override
    public String taskSearchText() {
        return taskSearchField.getText();
    }

    @Override
    public void clearTaskSearchText() {
        taskSearchField.setText("");
    }

    @Override
    public void setAddCategoryAction(Runnable action) {
        addCategoryButton.addActionListener(event -> action.run());
    }

    @Override
    public void setDeleteCategoryAction(Runnable action) {
        deleteCategoryButton.addActionListener(event -> action.run());
    }

    @Override
    public void setAddTaskAction(Runnable action) {
        addTaskButton.addActionListener(event -> action.run());
    }

    @Override
    public void setCompleteTaskAction(Runnable action) {
        completeTaskButton.addActionListener(event -> action.run());
    }

    @Override
    public void setDeleteTaskAction(Runnable action) {
        deleteTaskButton.addActionListener(event -> action.run());
    }

    @Override
    public void setUpdateCategoryAction(Runnable action) {
        updateCategoryButton.addActionListener(event -> action.run());
    }

    @Override
    public void setUpdateTaskAction(Runnable action) {
        updateTaskButton.addActionListener(event -> action.run());
    }

    @Override
    public void setPendingTaskAction(Runnable action) {
        pendingTaskButton.addActionListener(event -> action.run());
    }

    @Override
    public void setSearchTasksAction(Runnable action) {
        searchTasksButton.addActionListener(event -> action.run());
    }

    @Override
    public void setClearTaskSearchAction(Runnable action) {
        clearTaskSearchButton.addActionListener(event -> action.run());
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

    public JButton getDeleteCategoryButton() {
        return deleteCategoryButton;
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

    public JButton getUpdateCategoryButton() {
        return updateCategoryButton;
    }

    public JButton getUpdateTaskButton() {
        return updateTaskButton;
    }

    public JButton getPendingTaskButton() {
        return pendingTaskButton;
    }

    public JTextField getTaskSearchField() {
        return taskSearchField;
    }

    public JButton getSearchTasksButton() {
        return searchTasksButton;
    }

    public JButton getClearTaskSearchButton() {
        return clearTaskSearchButton;
    }

    private boolean confirmDeletion(int itemCount, String singularName, String pluralName) {
        String itemName = itemCount == 1 ? singularName : pluralName;

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete " + itemCount + " " + itemName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return result == JOptionPane.YES_OPTION;
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

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));

        bottomPanel.add(createTaskSearchPanel(), BorderLayout.NORTH);
        bottomPanel.add(createButtonsPanel(), BorderLayout.CENTER);

        return bottomPanel;
    }

    private JPanel createTaskSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        JPanel searchButtonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        searchButtonsPanel.add(searchTasksButton);
        searchButtonsPanel.add(clearTaskSearchButton);

        searchPanel.add(new JLabel("Task title:"), BorderLayout.WEST);
        searchPanel.add(taskSearchField, BorderLayout.CENTER);
        searchPanel.add(searchButtonsPanel, BorderLayout.EAST);

        return searchPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 4, 10, 5));

        buttonsPanel.add(addCategoryButton);
        buttonsPanel.add(updateCategoryButton);
        buttonsPanel.add(deleteCategoryButton);
        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(updateTaskButton);
        buttonsPanel.add(completeTaskButton);
        buttonsPanel.add(pendingTaskButton);
        buttonsPanel.add(deleteTaskButton);

        return buttonsPanel;
    }

    private JTable createCategoryTable() {
        SelectableTableModel model = new SelectableTableModel("Select", "ID", "Name");

        JTable table = new JTable(model);
        table.setName(CATEGORY_TABLE_NAME);
        configureCheckboxSelectionOnly(table);

        return table;
    }

    private JTable createTaskTable() {
        SelectableTableModel model = new SelectableTableModel(
                "Select",
                "ID",
                "Title",
                "Category",
                "Priority",
                "Deadline",
                "Status");

        JTable table = new JTable(model);
        table.setName(TASK_TABLE_NAME);
        configureCheckboxSelectionOnly(table);

        return table;
    }

    private void configureCheckboxSelectionOnly(JTable table) {
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
    }

    private JButton createButton(String text, String name) {
        JButton button = new JButton(text);
        button.setName(name);

        return button;
    }

    private DefaultTableModel tableModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    private String categoryName(StudyTask task) {
        if (task.getCategory() == null) {
            return "";
        }

        return task.getCategory().getName();
    }

    private List<Long> selectedIds(JTable table, int idColumnIndex) {
        stopTableEditing(table);

        List<Long> ids = new ArrayList<>();

        for (int row = 0; row < table.getRowCount(); row++) {
            int modelRow = table.convertRowIndexToModel(row);
            Object selectedValue = table.getModel().getValueAt(modelRow, 0);

            if (Boolean.TRUE.equals(selectedValue)) {
                ids.add(idAt(table, modelRow, idColumnIndex));
            }
        }

        return ids;
    }

    private Optional<Long> selectedRowId(JTable table, int idColumnIndex) {
        stopTableEditing(table);

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            return Optional.empty();
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        return Optional.of(idAt(table, modelRow, idColumnIndex));
    }

    private Long idAt(JTable table, int modelRow, int idColumnIndex) {
        Object idValue = table.getModel().getValueAt(modelRow, idColumnIndex);

        if (idValue instanceof Long id) {
            return id;
        }

        if (idValue instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(idValue.toString());
    }

    private void stopTableEditing(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private record CategoryItem(Long id, String name) {

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class SelectableTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        SelectableTableModel(String... columnNames) {
            super(columnNames, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }

            return Object.class;
        }
    }
}
