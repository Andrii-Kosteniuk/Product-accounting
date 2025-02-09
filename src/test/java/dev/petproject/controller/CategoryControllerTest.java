package dev.petproject.controller;


import dev.petproject.domain.Category;
import dev.petproject.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {


    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
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
}