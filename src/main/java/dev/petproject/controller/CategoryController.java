package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/saveCategory")
    public String addNewCategory(@ModelAttribute("category") Category category, Model model, HttpSession session) {
        categoryService.saveNewCategory(category);

        Product product = new Product();
        product.setId((Integer) session.getAttribute("id"));
        product.setName((String) session.getAttribute("name"));
        product.setPrice((Double) session.getAttribute("price"));
        product.setCategory(category);
        product.setDescription((String) session.getAttribute("description"));

        model.addAttribute("category", category);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/products/add?success=true";
    }

    @GetMapping("/addNewCategory")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showFormForAddingNewCategory(@ModelAttribute("product") Product product, Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("product", new Product());
        return "edit";
    }

}
