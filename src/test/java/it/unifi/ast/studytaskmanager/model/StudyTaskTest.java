package it.unifi.ast.studytaskmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class StudyTaskTest {

    @Test
    void createsPendingTaskByDefault() {
        Category category = new Category("Math");

        StudyTask task = new StudyTask(
                "Study algebra",
                "Revise linear equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                category);

        assertThat(task.getTitle()).isEqualTo("Study algebra");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getCategory()).isEqualTo(category);
    }

    @Test
    void marksTaskAsCompleted() {
        StudyTask task = new StudyTask(
                "Study algebra",
                "Revise linear equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                new Category("Math"));

        task.markCompleted();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void marksCompletedTaskAsPendingAgain() {
        StudyTask task = new StudyTask(
                "Study algebra",
                "Revise linear equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                new Category("Math"));

        task.markCompleted();
        task.markPending();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void trimsTaskTitle() {
        StudyTask task = new StudyTask(
                "  Study algebra  ",
                "Revise linear equations",
                Priority.HIGH,
                LocalDate.of(2026, 7, 20),
                new Category("Math"));

        assertThat(task.getTitle()).isEqualTo("Study algebra");
    }
}
