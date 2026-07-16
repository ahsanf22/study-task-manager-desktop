package it.unifi.ast.studytaskmanager.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ApplicationDatabasePropertiesTest {

    @Test
    void createsJpaProperties() {
        ApplicationDatabaseProperties databaseProperties =
                new ApplicationDatabaseProperties(
                        "jdbc:postgresql://localhost:5432/custom",
                        "user",
                        "password");

        Map<String, String> jpaProperties = databaseProperties.toJpaProperties();

        assertThat(jpaProperties)
                .containsEntry("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/custom")
                .containsEntry("jakarta.persistence.jdbc.user", "user")
                .containsEntry("jakarta.persistence.jdbc.password", "password")
                .containsEntry("jakarta.persistence.jdbc.driver", "org.postgresql.Driver")
                .containsEntry("hibernate.hbm2ddl.auto", "update");
    }
}
