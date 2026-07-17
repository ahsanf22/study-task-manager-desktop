package it.unifi.ast.studytaskmanager.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

class StudyTaskAdditionalCoverageTest {

    @Test
    void rejectsNullOrBlankTaskTitles() {
        Category category = new Category("Math");
        LocalDate deadline = LocalDate.of(2026, Month.JULY, 20);

        assertThatThrownBy(() -> new StudyTask(null, "Description", Priority.HIGH, deadline, category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");

        assertThatThrownBy(() -> new StudyTask("   ", "Description", Priority.HIGH, deadline, category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");
    }

    @Test
    void rejectsInvalidTitlesWhenRenamingOrUpdatingDetails() {
        StudyTask task = task();
        Category category = new Category("Science");
        LocalDate deadline = LocalDate.of(2026, Month.JULY, 21);

        assertThatThrownBy(() -> task.rename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");

        assertThatThrownBy(() -> task.updateDetails(
                "   ",
                "Description",
                Priority.MEDIUM,
                deadline,
                category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");
    }

    @Test
    void trimsTitleWhenRenaming() {
        StudyTask task = task();

        task.rename("  Study geometry  ");

        assertThat(task.getTitle()).isEqualTo("Study geometry");
    }

    private StudyTask task() {
        return new StudyTask(
                "Study algebra",
                "Description",
                Priority.HIGH,
                LocalDate.of(2026, Month.JULY, 20),
                new Category("Math"));
    }
}
