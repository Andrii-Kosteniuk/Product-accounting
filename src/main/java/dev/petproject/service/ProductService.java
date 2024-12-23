package dev.petproject.service;


import dev.petproject.domain.Product;
import dev.petproject.exception.ProductNotFoundException;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> searchProductsByKeyword(String keyword) {
        if (keyword != null) {
            return productRepository.findProductByKeyword(keyword);
        } else
            throw new IllegalArgumentException("Keyword is null");
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void changeQuantity(Integer id, Integer quantity) {
        productRepository.setNewAmount(id, quantity);
    }

    public Optional<Product> findProductById(Integer id) {
        return Optional.ofNullable(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + "not found")));
    }

    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findPaginated(int pageNo, int pageSize, String sortedField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortedField).ascending() : Sort.by(sortedField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAll(pageable);
    }


    public Optional<Product> findProductByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> allProducts() {
        return productRepository.findAll();
    }
}
