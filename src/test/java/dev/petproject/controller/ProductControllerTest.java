package dev.petproject.controller;


import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    ProductService productService;

    List<Product> products;

    @MockBean
    private CategoryService categoryService;


    @MockBean
    private BindingResult bindingResult;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, categoryService)).build();

        Category books = new Category(1, "Books", new ArrayList<>());
        Category vehicle = new Category(2, "Vehicles", new ArrayList<>());
        Category sport = new Category(3, "Sport", new ArrayList<>());

        products = List.of(
                new Product(1, "Java in practice", 10.0, 2, "Learn java in practice", books),
                new Product(2, "Mazda", 100000.0, 3, "Exclusive car", vehicle),
                new Product(3, "Ball", 15.0, 1, "For playing games", sport));
    }


    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldBeRepresentedAllProductsAccordingToSIzeOfPage() throws Exception {

        when(productService.findPaginated(anyInt(), anyInt(), anyString(), anyString())).thenReturn(new PageImpl<>(products));

        mockMvc.perform(get("/products/"))
                .andExpectAll(
                        status().isOk(),
                        view().name("products")
                );
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldShowEditPage() throws Exception {
        mockMvc.perform(get("/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldCreateNewProductAndSaveIt() throws Exception {
        Category electronics = new Category(1, "Electronics", new ArrayList<>());
        Product product = new Product(3, "Iphone", 5454.6, 5, "For calls", electronics);

        mockMvc.perform(post("/products/save"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"));

        productService.saveProduct(product);
        verify(productService, times(1)).saveProduct(product);
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldDoNothingIfProductDataIsIncorrect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        verifyNoInteractions(productService);

        mockMvc.perform(post("/products/save"))
                .andExpect(view().name("edit"));
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldChangeDataInProductAndSaveThem() throws Exception {
        Product editProduct = productService.findProductById(anyInt());
        when(productService.findProductById(anyInt())).thenReturn(editProduct);
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());
        when(productService.findProductById(anyInt())).thenReturn(any(Product.class));

        mockMvc.perform(get("/products/edit/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("category"))
                .andExpect(view().name("edit"));

    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(get("/products/delete/{id}", 1)
                        .sessionAttr("currentPage", 1)
                        .sessionAttr("sortField", "name")
                        .sessionAttr("sortDir", "asc"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldFindAllProductsThatIsMatchedToKeyword() throws Exception {
        productService.searchProductsByKeyword(anyString());
        verify(productService, times(1)).searchProductsByKeyword(anyString());

        mockMvc.perform(get("/products/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("products"));


    }

}