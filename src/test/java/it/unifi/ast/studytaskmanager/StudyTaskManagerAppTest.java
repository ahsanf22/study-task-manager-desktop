package it.unifi.ast.studytaskmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StudyTaskManagerAppTest {

    @Test
    void providesApplicationName() {
        assertThat(StudyTaskManagerApp.APPLICATION_NAME)
                .isEqualTo("Study Task Manager Desktop");
    }
}
