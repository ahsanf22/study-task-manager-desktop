package it.unifi.ast.studytaskmanager;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class StudyTaskManagerAppTest {

    @Test
    void applicationStarts() {
        assertThatCode(() -> StudyTaskManagerApp.main(new String[0]))
                .doesNotThrowAnyException();
    }
}
