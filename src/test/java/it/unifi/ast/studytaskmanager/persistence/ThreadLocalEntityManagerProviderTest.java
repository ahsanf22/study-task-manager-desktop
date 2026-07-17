package it.unifi.ast.studytaskmanager.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ThreadLocalEntityManagerProviderTest {

    @Test
    void rejectsAccessWhenNoEntityManagerIsBound() {
        ThreadLocalEntityManagerProvider provider = new ThreadLocalEntityManagerProvider();

        assertThatThrownBy(provider::getEntityManager)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No EntityManager bound to the current transaction");
    }
}
