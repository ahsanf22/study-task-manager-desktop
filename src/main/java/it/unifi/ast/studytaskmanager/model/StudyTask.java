package it.unifi.ast.studytaskmanager.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_tasks")
public class StudyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected StudyTask() {
    }

    public StudyTask(String title, String description, Priority priority, LocalDate deadline, Category category) {
        this.title = normalizeTitle(title);
        this.description = description;
        this.priority = Objects.requireNonNull(priority, "Priority must not be null");
        this.deadline = Objects.requireNonNull(deadline, "Deadline must not be null");
        this.category = Objects.requireNonNull(category, "Category must not be null");
        this.status = TaskStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void rename(String title) {
        this.title = normalizeTitle(title);
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Category getCategory() {
        return category;
    }

    public void updateDetails(
            String title,
            String description,
            Priority priority,
            LocalDate deadline,
            Category category) {
        this.title = normalizeTitle(title);
        this.description = description;
        this.priority = Objects.requireNonNull(priority, "Priority must not be null");
        this.deadline = Objects.requireNonNull(deadline, "Deadline must not be null");
        this.category = Objects.requireNonNull(category, "Category must not be null");
    }

    public void markCompleted() {
        status = TaskStatus.COMPLETED;
    }

    public void markPending() {
        status = TaskStatus.PENDING;
    }

    private String normalizeTitle(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title must not be empty");
        }

        return value.trim();
    }
}
