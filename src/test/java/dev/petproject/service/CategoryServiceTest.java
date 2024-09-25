package dev.petproject.service;

import dev.petproject.domain.Category;
import dev.petproject.exception.CategoryAlreadyExistsException;
import dev.petproject.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    CategoryService categoryService;

    private List<Category> categories;


    @BeforeEach
    void setUp() {
        categories = new ArrayList<>();

        Category vehicle = Category.builder()
                .id(1)
                .name("Vehicle")
                .build();
        Category eat = Category.builder()
                .id(2)
                .name("Eat")
                .build();
        categories.add(vehicle);
        categories.add(eat);
        categoryRepository.saveAll(Set.of(vehicle, eat));
    }

    @Test
    void givenNewCategory_whenSaveNewCategory_thenTheCategoryIsSavedProperly() {
        //Given
        Category newCategory = Category.builder()
                .id(3)
                .name("New Category")
                .build();

        //When
        categoryService.saveNewCategory(newCategory);

        //Then
        assertNotNull(categoryRepository.findById(3));
        verify(categoryRepository, times(1)).save(newCategory);
    }

    @Test
    void givenListCategory_whenGetAllCategories_thenReturnThisListOfCategories() {
        //Given
        when(categoryRepository.findAll()).thenReturn(categories);

        //When
        List<Category> categoryList = categoryService.getAllCategories();

        //Then
        assertNotNull(categoryList);
        assertEquals(2, categoryList.size());
        assertEquals("Vehicle", categoryList.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowAnException_whenAddNewCategoryThatAlreadyExists() {
        Category vehicle2 = Category.builder()
                .id(4)
                .name("Vehicle")
                .build();

        when(categoryRepository.existsByName("Vehicle")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.saveNewCategory(vehicle2));
        verify(categoryRepository, never()).save(any(Category.class));
    }


}