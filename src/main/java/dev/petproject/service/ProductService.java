package dev.petproject.service;


import dev.petproject.domain.Product;
import dev.petproject.exception.EmptySymbolException;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.exception.ProductNotFoundException;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> searchProductsByKeyword(String keyword) {
        if (! keyword.isEmpty()) {
            return productRepository.findProductByKeyword(keyword);
        } else
            throw new EmptySymbolException("Keyword is empty");
    }

    @CacheEvict(value = "products")
    public void saveProduct(Product product) {
        Objects.requireNonNull(product, "Product must not be null");
        log.info("Finding product: {}", product.getName());

        checkProductOnDuplicate(product);

        productRepository.save(product);
        log.info("Saving product: {}", product.getName());
    }

    public void checkProductOnDuplicate(Product product) {
        List<Product> existingProducts = productRepository.findByName(product.getName());

        Optional<Product> sameProduct = existingProducts.stream()
                .filter(p -> p.getName().equals(product.getName()) && p.getCategory().equals(product.getCategory()))
                .findAny();

        if (sameProduct.isPresent()) {
            log.error("Product already exists");
            throw new ProductAlreadyExistsException("Such a product already exists");
        }
    }

    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id '" + id + "' not found"));
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findPaginated(int pageNo, int pageSize, String sortedField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortedField).ascending() : Sort.by(sortedField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAllProducts(pageable);
    }
}
