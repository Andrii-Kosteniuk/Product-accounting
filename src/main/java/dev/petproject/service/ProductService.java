package dev.petproject.service;


import dev.petproject.domain.Product;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.exception.ProductNotFoundException;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> searchProductsByKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return productRepository.findProductByKeyword(keyword);
        } else
            throw new IllegalArgumentException("Keyword is null");
    }

    public void saveProduct(Product product) {
        Objects.requireNonNull(product, "Product must not be null");
        log.info("Finding product: {}", product.getName());

        Product foundProduct = productRepository.findByName(product.getName());
        if (foundProduct != null) {
            log.error("Throwing exception that product already exists");
            throw new ProductAlreadyExistsException("Product with name '" + foundProduct.getName() + "' already exists");
        }

        productRepository.save(product);
        log.info("Saving product: {}", product.getName());
    }

    public void updateProduct(Product product) {
        Product foundProduct = findProductById(product.getId());

        log.info("Finding product with id: {}", product.getId());
        Objects.requireNonNull(product, "Product is null");

        productRepository.save(foundProduct);
        log.info("Saving product : {}", foundProduct.getName());
    }

    @CachePut("products")
    public Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id '" + id + "' not found"));
    }

    @CachePut(value = "products")
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }

    @Cacheable("products")
    public Page<Product> findPaginated(int pageNo, int pageSize, String sortedField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortedField).ascending() : Sort.by(sortedField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAll(pageable);
    }

}
