package dev.petproject.dataInitializer;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.repository.CategoryRepository;
import dev.petproject.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {


    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dataInitializer, "numberOfCategoriesToCreate", 2);
        ReflectionTestUtils.setField(dataInitializer, "numberOfProductsToCreate", 3);
    }

    @Test
    void testRunMethod() {
        DataInitializer spyInitializer = spy(dataInitializer);
        doNothing().when(spyInitializer).initializeData();

        spyInitializer.run();

        verify(spyInitializer, times(1)).initializeData();
    }

    @Test
    void testSaveCategoryIfNotExists() {
        Category category = new Category(1, "Devices", new ArrayList<>());

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        dataInitializer.initializeData();

        verify(categoryRepository, atLeast(1)).save(any(Category.class));
    }

    @Test
    void testCategoryIsNotSavedIfAlreadyExists() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        dataInitializer.initializeData();

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testProductIsSavedIfNotExists() {
        Category category = new Category(1, "Electronics", new ArrayList<>());
        Product product = Product.builder().name("Laptop").category(category).build();

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(productRepository.exists(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        dataInitializer.initializeData();

        verify(productRepository, atLeast(1)).save(any(Product.class));
    }

    @Test
    void testCorrectNumberOfCategoriesCreated() {
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.initializeData();

        verify(categoryRepository, times(2)).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getAllValues()).hasSize(2);
    }

    @Test
    void testCorrectNumberOfProductsCreatedPerCategory() {
        Category category = new Category(1, "Electronics", new ArrayList<>());
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(productRepository.exists(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.initializeData();

        verify(productRepository, times(6)).save(productCaptor.capture());
        assertThat(productCaptor.getAllValues()).hasSize(6);
    }
}