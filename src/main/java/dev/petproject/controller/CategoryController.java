package dev.petproject.controller;

import dev.petproject.domain.Category;
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
            return "createCategory";
        }
    }

    @GetMapping("/addNewCategory")
    public String showFormForAddingNewCategory(Model model) {
        model.addAttribute("category", new Category());
        return "createCategory";
    }

}
