package it.unifi.ast.studytaskmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void createsCategoryWithName() {
        Category category = new Category("Math");

        assertThat(category.getName()).isEqualTo("Math");
    }

    @Test
    void trimsCategoryName() {
        Category category = new Category("  Math  ");

        assertThat(category.getName()).isEqualTo("Math");
    }

    @Test
    void categoriesWithSameIdAreEqual() {
        Category first = new Category("Math");
        Category second = new Category("Physics");

        first.setId(1L);
        second.setId(1L);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
    }
}
