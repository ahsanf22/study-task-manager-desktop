package it.unifi.ast.studytaskmanager.gui;

import java.time.LocalDate;

import it.unifi.ast.studytaskmanager.model.Priority;

public record TaskFormData(
        String title,
        String description,
        Priority priority,
        LocalDate deadline,
        Long categoryId) {
}
