package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.repository.ProductRepository;

import dev.petproject.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExcelController.class)
class ExcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @InjectMocks
    private ExcelController excelController;

    @Test
    @WithMockUser(username = "User", roles = "USER")
    void shouldExportToExcel() throws Exception {
        Category category = new Category();
        category.setName("Electronics");


        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product 1");
        product1.setPrice(100.0);
        product1.setDescription("Description 1");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product 2");
        product2.setPrice(200.0);
        product2.setDescription("Description 2");
        product2.setCategory(category);

        when(productRepository.count()).thenReturn(2L);
        when(productService.findPaginated(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PageImpl<>(Arrays.asList(product1, product2)));


         mockMvc.perform(get("/products/export-to-excel"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        verify(productService).findPaginated(1, 2, "name", "asc");
        verify(productRepository).count();
    }
}
