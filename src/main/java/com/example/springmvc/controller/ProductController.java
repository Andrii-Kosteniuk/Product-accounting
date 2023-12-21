package com.example.springmvc.controller;

import com.example.springmvc.model.Product;
import com.example.springmvc.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ProductController {

    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(path = "/products/add")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        return "edit";
    }

    @PostMapping("products")
    public String saveProduct(Product product) {
        productRepository.save(product);
        return "redirect:/";
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
