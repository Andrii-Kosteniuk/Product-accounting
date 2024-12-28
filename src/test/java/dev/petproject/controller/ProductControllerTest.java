package dev.petproject.controller;

import dev.petproject.auth.JwtUtils;
import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.repository.ProductRepository;
import dev.petproject.repository.TokenRepository;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    ProductService productService;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    TokenRepository tokenRepository;

    List<Product> products;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private Model model;

    @MockBean
    private BindingResult bindingResult;

    @MockBean
    private ProductController productController;

    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, categoryService)).build();

        Category books = new Category(1, "Books", new ArrayList<>());
        Category vehicle = new Category(2, "Vehicles", new ArrayList<>());
        Category sport = new Category(3, "Sport", new ArrayList<>());

        products = List.of(
                new Product(1, "Java in practice", "Learn java in practice", books, 10.0),
                new Product(2, "Mazda", "Exclusive car", vehicle, 100000.0),
                new Product(3, "Ball", "For playing games", sport, 15.0));
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldRepresentMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpectAll(
                        status().isOk(),
                        view().name("index")
                );
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldBeRepresentedAllProductsAccordingToSIzeOfPage() throws Exception {

        when(productService.findPaginated(anyInt(), anyInt(), anyString(), anyString())).thenReturn(new PageImpl<>(products));

        mockMvc.perform(get("/products/all"))
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
    void shouldCreateNewProductAndSaveIt() throws Exception, ProductAlreadyExistsException {
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
        Product editProduct = productService.findProductById(1);
        when(productService.findProductById(anyInt())).thenReturn(editProduct);
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/products/edit/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("edit"));

    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldDeleteProduct() throws Exception {

        productService.deleteProductById(anyInt());
        verify(productService, times(1)).deleteProductById(anyInt());

        mockMvc.perform(get("/products/delete/{id}", 1))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products/all"));
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