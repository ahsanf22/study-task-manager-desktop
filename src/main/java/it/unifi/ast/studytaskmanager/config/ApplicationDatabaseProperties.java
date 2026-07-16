package it.unifi.ast.studytaskmanager.config;

import java.util.HashMap;
import java.util.Map;

public class ApplicationDatabaseProperties {

    static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:5433/study_task_manager";
    static final String DEFAULT_USERNAME = "study";
    static final String DEFAULT_PASSWORD = "study";

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public ApplicationDatabaseProperties(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public static ApplicationDatabaseProperties fromEnvironment() {
        return new ApplicationDatabaseProperties(
                value("STUDY_TASK_MANAGER_DB_URL", DEFAULT_JDBC_URL),
                value("STUDY_TASK_MANAGER_DB_USER", DEFAULT_USERNAME),
                value("STUDY_TASK_MANAGER_DB_PASSWORD", DEFAULT_PASSWORD));
    }

    public Map<String, String> toJpaProperties() {
        Map<String, String> properties = new HashMap<>();

        properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
        properties.put("jakarta.persistence.jdbc.user", username);
        properties.put("jakarta.persistence.jdbc.password", password);
        properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        return properties;
    }

    private static String value(String environmentVariableName, String defaultValue) {
        String value = System.getenv(environmentVariableName);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }
}
