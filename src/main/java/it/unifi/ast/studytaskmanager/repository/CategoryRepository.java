package it.unifi.ast.studytaskmanager.repository;

import java.util.List;
import java.util.Optional;

import it.unifi.ast.studytaskmanager.model.Category;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    boolean existsById(Long id);

    Category save(Category category);

    void deleteById(Long id);
}
