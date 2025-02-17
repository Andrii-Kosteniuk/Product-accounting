package dev.petproject.controller;


import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.exception.EmptySymbolException;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.exception.ProductNotFoundException;
import dev.petproject.exception.advice.ProductControllerAdvice;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private Model model;

    private List<Product> products;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private HttpSession session;


    @MockBean
    private BindingResult bindingResult;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductControllerAdvice productControllerAdvice;


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
    @WithMockUser()
    void shouldCreateNewProductAndSaveIt() throws Exception {

        mockMvc.perform(post("/products/save")
                        .param("name", "New Product")
                        .param("price", "10.0")
                        .param("quantity", "5")
                        .param("description", "This is a test product")
                        .param("category.id", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products?success=true"));

        verify(productService, times(1)).saveProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldDoNothingIfProductDataIsIncorrect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        mockMvc.perform(post("/products/save"))
                .andExpect(view().name("edit"));

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldChangeDataInProductAndSaveThem() throws Exception {
        when(productService.findProductById(1)).thenReturn(products.get(0));
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

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

    @Test
    void shouldThrowProductNotFoundExceptionWhenSaveNewProduct() throws Exception {
        int invalidProductId = 999;
        when(productService.findProductById(invalidProductId))
                .thenThrow(new ProductNotFoundException("Product with id " + invalidProductId + " not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/edit/{id}", invalidProductId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEditViewWhenProductAlreadyExistsExceptionOccurs() {
        ProductAlreadyExistsException exception = new ProductAlreadyExistsException("Such a product already exists");
        ModelAndView modelAndView = productControllerAdvice.handleProductAlreadyExistsException(exception, model, session);

        assertEquals("edit", modelAndView.getViewName());
        assertEquals("Such a product already exists", modelAndView.getModel().get("errorCreateProduct"));
        verify(model, times(1)).addAttribute(eq("product"), any());
        verify(model, times(1)).addAttribute(eq("category"), any());
    }

    @Test
    void shouldThrowEmptySymbolExceptionWhenKeywordIsIncorrect() {
        EmptySymbolException exception = new EmptySymbolException("Keyword is empty");
        ModelAndView modelAndView = productControllerAdvice.handleEmptySymbolException(exception);

        assertEquals("products", modelAndView.getViewName());
        assertEquals("Keyword is empty", modelAndView.getModel().get("errorSearch"));
    }

    @Test
    void shouldRedirectTo404WithErrorMessageWhenProductNotFoundExceptionOccurs() {
        String errorMessage = "Product with id '" + 9999 + "' not found";
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);

        Model model = new ConcurrentModel();

        String viewName = productControllerAdvice.handleProductNotFoundException(exception, model);

        assertEquals("redirect:/404?error=true&message=" + errorMessage, viewName);
        assertEquals(errorMessage, model.getAttribute("productNotFound"));
    }

}