# Study Task Manager Desktop

[![Maven CI](https://github.com/ahsanf22/study-task-manager-desktop/actions/workflows/maven.yml/badge.svg)](https://github.com/ahsanf22/study-task-manager-desktop/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/ahsanf22/study-task-manager-desktop/badge.svg?branch=master)](https://coveralls.io/github/ahsanf22/study-task-manager-desktop?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ahsanf22_study-task-manager-desktop&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ahsanf22_study-task-manager-desktop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ahsanf22_study-task-manager-desktop&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ahsanf22_study-task-manager-desktop)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ahsanf22_study-task-manager-desktop&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ahsanf22_study-task-manager-desktop)

Study Task Manager Desktop is a Java Swing desktop application for managing study categories and study tasks.

The project was developed for the **Automated Software Testing** course as a **6-credit Java desktop application**. It follows a layered architecture and includes PostgreSQL persistence, JPA/Hibernate, transaction management, Docker/Testcontainers integration testing, and automated tests.

---

## Features

- Add study categories
- Update study categories
- Delete one or more selected categories
- Add study tasks
- Update study tasks
- Complete one or more selected tasks
- Mark one or more selected tasks as pending
- Delete one or more selected tasks
- Search tasks by title
- Checkbox-based multi-selection
- Confirmation dialogs before deleting tasks or categories
- PostgreSQL database persistence
- JPA/Hibernate repository implementation
- Service-layer transaction management
- Unit tests and integration tests
- PostgreSQL Testcontainers support

---

## Architecture

The project follows a layered architecture:

```text
Swing GUI
   ↓
Presenter
   ↓
Service Layer
   ↓
Repository Interfaces
   ↓
JPA / Hibernate Repositories
   ↓
PostgreSQL Database
```

The GUI layer is responsible only for user interaction.

The presenter coordinates user actions and updates the view.

The service layer contains the application logic and manages transactions.

The repository layer abstracts data access.

The persistence layer implements repositories using JPA/Hibernate.

---

## Main Technologies

- Java 21
- Maven
- Java Swing
- PostgreSQL
- JPA / Hibernate
- Docker Compose
- Testcontainers
- JUnit 5
- Mockito
- AssertJ
- JaCoCo
- PIT Mutation Testing

---

## Project Structure

```text
src/main/java/it/unifi/ast/studytaskmanager
├── config
│   ├── ApplicationConfiguration.java
│   └── ApplicationDatabaseProperties.java
├── gui
│   ├── MainFrame.java
│   ├── StudyTaskManagerPanel.java
│   ├── StudyTaskManagerView.java
│   └── TaskFormData.java
├── model
│   ├── Category.java
│   ├── Priority.java
│   ├── StudyTask.java
│   └── TaskStatus.java
├── persistence
│   ├── EntityManagerProvider.java
│   ├── JpaCategoryRepository.java
│   ├── JpaStudyTaskRepository.java
│   ├── JpaTransactionManager.java
│   └── ThreadLocalEntityManagerProvider.java
├── presenter
│   └── StudyTaskManagerPresenter.java
├── repository
│   ├── CategoryRepository.java
│   └── StudyTaskRepository.java
├── service
│   ├── CategoryService.java
│   └── StudyTaskService.java
├── transaction
│   └── TransactionManager.java
└── StudyTaskManagerApp.java
```

Test helper classes are located under `src/test/java`, including:

```text
src/test/java/it/unifi/ast/studytaskmanager/transaction
└── ImmediateTransactionManager.java
```

---

## Domain Model

The application manages two main entities:

### Category

A category represents a study area, for example:

- Math
- Software Testing
- Databases
- Resiliency

Each task belongs to one category.

### StudyTask

A study task contains:

- Title
- Description
- Priority
- Deadline
- Status
- Category

A task can be marked as completed or kept pending.

---

## Business Rules

The service layer enforces the main business rules:

- A category name must be unique.
- A task must belong to an existing category.
- A category cannot be deleted if it is still used by one or more tasks.
- A task can be marked as completed.
- One or more tasks can be completed together.
- One or more tasks can be deleted together.
- One or more unused categories can be deleted together.

---

## Transaction Management

Transactions are handled in the service layer through a `TransactionManager` abstraction.

This keeps transaction boundaries outside the GUI and repository logic.

Example flow:

```text
Presenter receives user action
   ↓
Service method starts transaction
   ↓
Repository performs data operation
   ↓
Transaction commits or rolls back
```

The JPA implementation uses `JpaTransactionManager`.

For unit tests, `ImmediateTransactionManager` is used to execute service code without a real database transaction.

---

## Database

The application uses PostgreSQL.

For local application execution, PostgreSQL is started with Docker Compose.

For integration tests, PostgreSQL is started automatically using Testcontainers.

---

## Default Database Configuration

The default runtime database connection is:

```text
JDBC URL: jdbc:postgresql://localhost:5433/study_task_manager
Username: study
Password: study
```

The port `5433` is used to avoid conflicts with a local PostgreSQL installation that may already be using port `5432`.

---

## Environment Variables

The database configuration can be overridden using these environment variables:

```bash
STUDY_TASK_MANAGER_DB_URL
STUDY_TASK_MANAGER_DB_USER
STUDY_TASK_MANAGER_DB_PASSWORD
```

Example:

```bash
export STUDY_TASK_MANAGER_DB_URL=jdbc:postgresql://localhost:5433/study_task_manager
export STUDY_TASK_MANAGER_DB_USER=study
export STUDY_TASK_MANAGER_DB_PASSWORD=study
```

---

## Running PostgreSQL

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Check that it is running:

```bash
docker compose ps
```

Stop PostgreSQL:

```bash
docker compose down
```

Do not use this unless you want to delete the database data:

```bash
docker compose down -v
```

---

## Running the Application

Start PostgreSQL first:

```bash
docker compose up -d postgres
```

Run the Swing application:

```bash
mvn exec:java -Dexec.mainClass=it.unifi.ast.studytaskmanager.StudyTaskManagerApp
```

---

## Running the Tests

Run the complete test suite:

```bash
mvn clean verify
```

The integration tests automatically start a PostgreSQL container using Testcontainers.

Docker must be running before executing the tests.

---

## Testing Strategy

The project includes several levels of testing.

### Model Tests

Model tests verify the behavior of domain objects such as `Category` and `StudyTask`.

### Service Tests

Service tests verify business logic, validation rules, and transaction usage.

### Presenter Tests

Presenter tests verify that user actions are correctly translated into service calls and view updates.

### GUI Panel Tests

Swing panel tests verify table structure, buttons, checkbox selection, and UI behavior without opening the full application window.

### Persistence Integration Tests

Persistence tests verify JPA/Hibernate repositories and transaction behavior using a real PostgreSQL database started by Testcontainers.

---

## Current Test Status

Current local test suite:

```text
66 passing tests
0 failures
0 errors
0 skipped
```

---

## Main User Actions

The application currently supports:

### Category Actions

- Add category
- Update selected category
- Delete selected category
- Delete multiple selected categories using checkboxes

### Task Actions

- Add task
- Update selected task
- Complete selected task
- Complete multiple selected tasks using checkboxes
- Mark selected task as pending
- Mark multiple selected tasks as pending using checkboxes
- Delete selected task
- Search tasks by title
- Delete multiple selected tasks using checkboxes

---

## Checkbox Selection

The application uses checkbox-based selection instead of row highlighting.

This avoids confusion when selecting multiple tasks or categories.

Rows are not highlighted when clicked. Users select items only by checking the checkbox in the first column.

---

## Delete Confirmation

Before deleting tasks or categories, the application asks for confirmation.

This prevents accidental deletion, especially when multiple checkboxes are selected.

---

## Build

Compile and test the project:

```bash
mvn clean verify
```

Build the JAR:

```bash
mvn clean package
```

The generated JAR is available in:

```text
target/study-task-manager-desktop-1.0.0-SNAPSHOT.jar
```

---

## Mutation Testing

The project includes PIT mutation testing configuration for the service layer.

Run mutation testing with:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

The PIT report is generated in:

```text
target/pit-reports/index.html
```

---

## GitHub Actions

The project includes a GitHub Actions workflow for continuous integration.

The workflow runs:

```bash
mvn clean verify
```

on every push and pull request.

---

## Notes for the Course

This project intentionally uses **Java Swing** because the 6-credit version of the course requires a desktop application.

The project also demonstrates:

- Layered architecture
- Test-driven development style
- Database persistence
- JPA/Hibernate
- Explicit transaction management
- Docker/Testcontainers integration testing
- Automated build with Maven
- GUI testing
- Presenter-based GUI separation

---

## Author

Muhammad Ahsan Khan

MSc Software Science and Technology  
University of Florence