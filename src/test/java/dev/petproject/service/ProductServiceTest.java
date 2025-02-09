package dev.petproject.service;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.exception.EmptySymbolException;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    List<Product> products;

    @BeforeEach
    void setUp() {
        Category books = new Category(1, "Books", new ArrayList<>());
        Category vehicle = new Category(2, "Vehicles", new ArrayList<>());
        Category music = new Category(3, "Music", new ArrayList<>());


        Product book = Product.builder()
                .id(1)
                .name("Java in practice")
                .price(10.0)
                .description("Learn java in practice")
                .category(books)
                .build();

        Product car = Product.builder()
                .id(2)
                .name("Mazda")
                .price(100000.0)
                .description("Exclusive car")
                .category(vehicle)
                .build();

        Product compactDisk = Product.builder()
                .id(3)
                .name("The scorpions")
                .price(35.0)
                .description("Set of music songs")
                .category(music)
                .build();

        products = new ArrayList<>();
        products.add(book);
        products.add(car);
        products.add(compactDisk);

    }


    @Test
    void shouldFindProductsByKeyword() {
        String keyword = "Java";
        List<Product> productsExpected = List.of(products.get(0));

        when(productRepository.findProductByKeyword(keyword))
                .thenReturn(productsExpected);

        List<Product> searched = productService.searchProductsByKeyword(keyword);

        assertEquals(searched, productsExpected);
        assertTrue(searched.contains(products.get(0)));
    }

    @Test
    void shouldSaveProductIntoDatabase() throws ProductAlreadyExistsException {
        Category sport = new Category(3, "Sport", new ArrayList<>());
        Product newProduct = Product.builder()
                .id(5)
                .name("Ball")
                .price(15.0)
                .description("For playing different games")
                .category(sport)
                .build();

        when(productRepository.save(newProduct)).thenReturn(newProduct);

        productService.saveProduct(newProduct);

        List<Product> allProducts = List.of(newProduct);
        when(productRepository.findAll()).thenReturn(allProducts);

        List<Product> retrievedProducts = productRepository.findAll();


        assertThat(retrievedProducts).isNotEmpty();
        assertTrue(retrievedProducts.contains(newProduct));

        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenKeywordIsNull() {

        EmptySymbolException exception = assertThrows(EmptySymbolException.class, () -> productService.searchProductsByKeyword(""));

        Assertions.assertEquals("Keyword is empty", exception.getMessage());
        verify(productRepository, never()).findProductByKeyword(null);
    }

    @Test
    void findProductById() {
        Product findProduct = products.get(0);
        when(productRepository.findById(1)).thenReturn(Optional.of(findProduct));

        Product productById = productService.findProductById(1);

        assertNotNull(productById);
        assertEquals(productById, findProduct, "Products are not the same");
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void deleteProductById() {
        productService.deleteProductById(1);

        verify(productRepository, times(1)).deleteById(1);

    }

    @Test
    void findPaginated() {
        int pageNo = 1;
        int pageSize = 5;
        String sortField = "name";
        String dir = "asc";

        // Given

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortField).ascending());
        Page<Product> mockPage  = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAllProducts(pageable)).thenReturn(mockPage);

        // When

        Page<Product> paginated = productService.findPaginated(pageNo, pageSize, sortField, dir);

        // Assert
        assertNotNull(paginated);
        assertEquals(3, paginated.getContent().size());
        assertEquals("Java in practice", paginated.getContent().get(0).getName());

        verify(productRepository, times(1)).findAllProducts(pageable);

    }


}