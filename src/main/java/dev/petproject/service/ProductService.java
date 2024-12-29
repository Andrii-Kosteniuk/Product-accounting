package dev.petproject.service;


import dev.petproject.domain.Product;
import dev.petproject.exception.ProductNotFoundException;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> searchProductsByKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return productRepository.findProductByKeyword(keyword);
        } else
            throw new IllegalArgumentException("Keyword is null");
    }

    @CachePut(value = "products")
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Cacheable("products")
    public Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + "not found"));
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
