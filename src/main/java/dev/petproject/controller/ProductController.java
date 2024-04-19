package dev.petproject.controller;

import dev.petproject.domain.Product;
import dev.petproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/products/add")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        return "edit";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product) {
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products";

    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("product", productRepository.findById(id));
        return "edit";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}
