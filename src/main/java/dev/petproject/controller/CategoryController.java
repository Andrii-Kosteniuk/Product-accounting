package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.exception.CategoryAlreadyExistsException;
import dev.petproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
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
    public String addNewCategory(@ModelAttribute("category") Category category, Model model) {
        try {
            categoryService.saveNewCategory(category);
            model.addAttribute("category", category);
            return "redirect:/products/add?success";
        } catch (CategoryAlreadyExistsException e) {
            model.addAttribute("categoryException", e);
            return "edit";
        }
    }

    @GetMapping("/addNewCategory")
    public String showFormForAddingNewCategory(@ModelAttribute("product") Product product, Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("product", new Product());
        return "createCategory";
    }

}
