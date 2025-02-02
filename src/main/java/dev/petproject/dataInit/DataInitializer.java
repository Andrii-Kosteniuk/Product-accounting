package dev.petproject.dataInit;

import com.github.javafaker.Faker;
import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.repository.CategoryRepository;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final Faker faker = new Faker();

    @Value("${spring.app.numberOfCategories}")
    private int numberOfCategoriesToCreate;
    @Value("${spring.app.numberOfProducts}")
    private int numberOfProductsToCreate;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        initializeData();
    }

    private void initializeData() {
        for (int i = 0; i < numberOfCategoriesToCreate; i++) {
            Category category = createRandomCategory();
            log.info("Category created: {}", category);
            categoryRepository.save(category);


            for (int j = 0; j < numberOfProductsToCreate; j++) {
                Product product = createRandomProduct(category);
                log.info("Product created: {}", product);
                productRepository.save(product);
            }
        }
        log.info("Database initialized with random categories and products.");
    }

    private Category createRandomCategory() {
        return Category.builder()
                .name(faker.commerce().department())
                .build();
    }

    private Product createRandomProduct(Category category) {
        String sentence = faker.lorem().sentence(5);
        return Product.builder()
                .name(faker.commerce().productName())
                .price(Double.parseDouble(faker.commerce().price().replace(",", ".")))
                .quantity(faker.number().numberBetween(1, 100))
                .description(sentence.length() > 50 ? sentence.substring(0, 50) : sentence)
                .category(category)
                .build();
    }
}
