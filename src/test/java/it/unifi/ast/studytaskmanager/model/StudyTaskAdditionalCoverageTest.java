package it.unifi.ast.studytaskmanager.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class StudyTaskAdditionalCoverageTest {

    @Test
    void rejectsNullOrBlankTaskTitles() {
        Category category = new Category("Math");

        assertThatThrownBy(() -> new StudyTask(null, "Description", Priority.HIGH, LocalDate.now(), category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");

        assertThatThrownBy(() -> new StudyTask("   ", "Description", Priority.HIGH, LocalDate.now(), category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");
    }

    @Test
    void rejectsInvalidTitlesWhenRenamingOrUpdatingDetails() {
        StudyTask task = task();

        assertThatThrownBy(() -> task.rename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task title must not be empty");

        assertThatThrownBy(() -> task.updateDetails(
                "   ",
                "Description",
                Priority.MEDIUM,
                LocalDate.of(2026, 7, 21),
                new Category("Science")))
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
                LocalDate.of(2026, 7, 20),
                new Category("Math"));
    }
}
