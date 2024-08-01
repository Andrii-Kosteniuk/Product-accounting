package dev.petproject.service;

import dev.petproject.domain.Product;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductsByKeyword(String keyword) {
        if (keyword != null) {
            return productRepository.findProductByKeyword(keyword);
        }
        return productRepository.findAll();
    }

    public boolean isPositive(Double number) {
        return number >= 0;
    }
}
