package it.unifi.ast.studytaskmanager.service;

import java.util.List;

import it.unifi.ast.studytaskmanager.exception.CategoryInUseException;
import it.unifi.ast.studytaskmanager.exception.DuplicateCategoryNameException;
import it.unifi.ast.studytaskmanager.exception.ResourceNotFoundException;
import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import it.unifi.ast.studytaskmanager.transaction.TransactionManager;

public class CategoryService {

    private final TransactionManager transactionManager;
    private final CategoryRepository categoryRepository;
    private final StudyTaskRepository studyTaskRepository;

    public CategoryService(
            TransactionManager transactionManager,
            CategoryRepository categoryRepository,
            StudyTaskRepository studyTaskRepository) {
        this.transactionManager = transactionManager;
        this.categoryRepository = categoryRepository;
        this.studyTaskRepository = studyTaskRepository;
    }

    public List<Category> findAll() {
        return transactionManager.doInTransaction(categoryRepository::findAll);
    }

    public Category findById(Long id) {
        return transactionManager.doInTransaction(() -> categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)));
    }

    public Category createCategory(String name) {
        return transactionManager.doInTransaction(() -> {
            Category category = new Category(name);

            categoryRepository.findByName(category.getName())
                    .ifPresent(existing -> {
                        throw new DuplicateCategoryNameException(
                                "Category already exists with name: " + category.getName());
                    });

            return categoryRepository.save(category);
        });
    }

    public Category updateCategory(Long id, String name) {
        return transactionManager.doInTransaction(() -> {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

            Category renamedCategory = new Category(name);

            categoryRepository.findByName(renamedCategory.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new DuplicateCategoryNameException(
                                "Category already exists with name: " + renamedCategory.getName());
                    });

            category.rename(name);
            return categoryRepository.save(category);
        });
    }

    public void deleteCategory(Long id) {
        transactionManager.doInTransaction(() -> {
            if (!categoryRepository.existsById(id)) {
                throw new ResourceNotFoundException("Category not found with id: " + id);
            }

            if (studyTaskRepository.existsByCategoryId(id)) {
                throw new CategoryInUseException("Cannot delete category because it is used by existing tasks.");
            }

            categoryRepository.deleteById(id);
        });
    }
}
