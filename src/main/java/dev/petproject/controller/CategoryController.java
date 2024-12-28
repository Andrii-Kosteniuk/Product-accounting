package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save")
    public String createCategory(@ModelAttribute("category") Category category, Model model, HttpSession session) {

        try {
            categoryService.saveNewCategory(category);
            model.addAttribute("category", category);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "redirect:/products/add?success=true";
        } catch (Exception e) {
            return "redirect:/products/add";
        }


    }

}
