package com.example.springmvc.controller;

import com.example.springmvc.model.Product;
import com.example.springmvc.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/products")
    public String saveProduct(Product product) {
        productRepository.save(product);
        return "redirect:/";
    }
}
