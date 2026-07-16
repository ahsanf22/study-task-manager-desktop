package it.unifi.ast.studytaskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import it.unifi.ast.studytaskmanager.exception.CategoryInUseException;
import it.unifi.ast.studytaskmanager.exception.DuplicateCategoryNameException;
import it.unifi.ast.studytaskmanager.exception.ResourceNotFoundException;
import it.unifi.ast.studytaskmanager.model.Category;
import it.unifi.ast.studytaskmanager.repository.CategoryRepository;
import it.unifi.ast.studytaskmanager.repository.StudyTaskRepository;
import it.unifi.ast.studytaskmanager.transaction.ImmediateTransactionManager;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private StudyTaskRepository studyTaskRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        studyTaskRepository = Mockito.mock(StudyTaskRepository.class);
        categoryService = new CategoryService(
                new ImmediateTransactionManager(),
                categoryRepository,
                studyTaskRepository);
    }

    @Test
    void findsAllCategories() {
        Category category = new Category("Math");

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        assertThat(categoryService.findAll()).containsExactly(category);
    }

    @Test
    void findsCategoryById() {
        Category category = new Category("Math");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThat(categoryService.findById(1L)).isEqualTo(category);
    }

    @Test
    void throwsWhenCategoryIsNotFoundById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");
    }

    @Test
    void createsCategoryWhenNameIsUnique() {
        Category savedCategory = new Category("Math");
        savedCategory.setId(1L);

        when(categoryRepository.findByName("Math")).thenReturn(Optional.empty());
        when(categoryRepository.save(argThat(category -> "Math".equals(category.getName())))).thenReturn(savedCategory);

        Category createdCategory = categoryService.createCategory("  Math  ");

        assertThat(createdCategory).isEqualTo(savedCategory);
        verify(categoryRepository).save(argThat(category -> "Math".equals(category.getName())));
    }

    @Test
    void rejectsDuplicateCategoryName() {
        when(categoryRepository.findByName("Math")).thenReturn(Optional.of(new Category("Math")));

        assertThatThrownBy(() -> categoryService.createCategory("Math"))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessage("Category already exists with name: Math");

        verify(categoryRepository, never()).save(argThat(category -> "Math".equals(category.getName())));
    }

    @Test
    void updatesCategoryWhenNameIsUnique() {
        Category category = new Category("Math");
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Physics")).thenReturn(Optional.empty());
        when(categoryRepository.save(category)).thenReturn(category);

        Category updatedCategory = categoryService.updateCategory(1L, "Physics");

        assertThat(updatedCategory.getName()).isEqualTo("Physics");
        verify(categoryRepository).save(category);
    }

    @Test
    void rejectsUpdateWhenCategoryNameBelongsToAnotherCategory() {
        Category category = new Category("Math");
        category.setId(1L);

        Category existingCategory = new Category("Physics");
        existingCategory.setId(2L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Physics")).thenReturn(Optional.of(existingCategory));

        assertThatThrownBy(() -> categoryService.updateCategory(1L, "Physics"))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessage("Category already exists with name: Physics");

        verify(categoryRepository, never()).save(category);
    }

    @Test
    void deletesCategoryWhenItExistsAndIsNotUsed() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(studyTaskRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void rejectsDeletingMissingCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");

        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    void rejectsDeletingCategoryUsedByTasks() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(studyTaskRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessage("Cannot delete category because it is used by existing tasks.");

        verify(categoryRepository, never()).deleteById(1L);
    }
}
