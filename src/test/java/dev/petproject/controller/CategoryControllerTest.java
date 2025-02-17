package dev.petproject.controller;


import dev.petproject.domain.Category;
import dev.petproject.exception.CategoryAlreadyExistsException;
import dev.petproject.exception.advice.CategoryControllerAdvice;
import dev.petproject.repository.CategoryRepository;
import dev.petproject.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {


    @MockBean
    private CategoryService categoryService;

    @MockBean
    private Model model;

    @Autowired
    private CategoryControllerAdvice categoryControllerAdvice;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BindingResult result;

    @MockBean
    private HttpSession session;
    @MockBean
    private CategoryRepository categoryRepository;


    @Test
    @WithMockUser
    void createCategory() throws Exception {
        // Given
        Category device = Category.builder()
                .name("Iphone")
                .products(new ArrayList<>())
                .build();

        doNothing().when(categoryService).saveNewCategory(device);

        // When & Then
        mockMvc.perform(post("/save-category")
                        .with(csrf())
                        .param("name", "Iphone"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/add?success=true"));

        verify(categoryService, times(1)).saveNewCategory(any(Category.class));
    }

    @Test
    @WithMockUser
    void testValidationErrorsOccurWhenInvalidData() throws Exception {
        mockMvc.perform(post("/save-category")
                        .param("invalidName", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("fieldErrorMessage", "The category name you provided is incorrect... Try to provide another one!"));

    }


    @Test
    void shouldReturnEditViewWhenCategoryAlreadyExistsExceptionOccurs() {
        CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException("Such a category already exists");

        ModelAndView modelAndView = categoryControllerAdvice.handleCategoryAlreadyExistsException(exception, model, session);

        Assertions.assertNotNull(modelAndView, "ModelAndView should not be null");
        Assertions.assertEquals("edit", modelAndView.getViewName());
        Assertions.assertEquals("Such a category already exists", modelAndView.getModel().get("errorCreateNewCategory"));
        verify(model, times(1)).addAttribute(eq("product"), any());
        verify(model, times(2)).addAttribute(eq("category"), any());
    }
}