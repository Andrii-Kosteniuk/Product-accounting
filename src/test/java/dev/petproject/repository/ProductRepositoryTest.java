package dev.petproject.repository;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        Category electronics = Category.builder()
                .name("Electronics")
                .build();

        Product product1 = Product.builder()
                .name("iPhone 12")
                .description("Latest Apple smartphone")
                .category(electronics)
                .price(999.99)
                .build();
        Product product2 = Product.builder()
                .name("Samsung Galaxy S21")
                .description("Latest Samsung smartphone")
                .category(electronics)
                .price(849.99)
                .build();

        Product product3 = Product.builder()
                .name("Apple Watch")
                .price(10000.0)
                .description("Its a good watch")
                .category(electronics)
                .build();

        categoryRepository.save(electronics);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

    }

    @Test
    void givenKeyword_WhenFindByKeyword_ThenReturnListOfProducts() {
        String keyWord = "iPhone";
        List<Product> products = productRepository.findProductByKeyword(keyWord);

        assertEquals(1, products.size());
        assertEquals("iPhone 12", products.get(0).getName());

    }

    @Test
    public void testFindProductByKeywordNoResults() {
        String keyword = "NonExistingProduct";
        List<Product> products = productRepository.findProductByKeyword(keyword);

        assertTrue(products.isEmpty());
    }
}