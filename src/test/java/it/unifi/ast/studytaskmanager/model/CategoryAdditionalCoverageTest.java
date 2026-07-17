package it.unifi.ast.studytaskmanager.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CategoryAdditionalCoverageTest {

    @Test
    void rejectsNullOrBlankCategoryNames() {
        assertThatThrownBy(() -> new Category(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name must not be empty");

        assertThatThrownBy(() -> new Category("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name must not be empty");
    }

    @Test
    void rejectsNullOrBlankNamesWhenRenaming() {
        Category category = new Category("Math");

        assertThatThrownBy(() -> category.rename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name must not be empty");

        assertThatThrownBy(() -> category.rename("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name must not be empty");
    }

    @Test
    void comparesCategoriesByIdentifier() {
        Category category = new Category("Math");
        category.setId(1L);

        Category sameIdentifier = new Category("Math Copy");
        sameIdentifier.setId(1L);

        Category differentIdentifier = new Category("Science");
        differentIdentifier.setId(2L);

        Category unsavedCategory = new Category("History");
        Category anotherUnsavedCategory = new Category("Unsaved Copy");

        assertThat(category)
                .isEqualTo(sameIdentifier)
                .isNotEqualTo(differentIdentifier)
                .isNotEqualTo(unsavedCategory);

        assertThat(category.equals(category)).isTrue();
        assertThat(category.equals("Math")).isFalse();
        assertThat(category.equals(null)).isFalse();
        assertThat(unsavedCategory.equals(category)).isFalse();
        assertThat(unsavedCategory.equals(anotherUnsavedCategory)).isFalse();
        assertThat(category.hashCode()).isEqualTo(31);
    }
}
