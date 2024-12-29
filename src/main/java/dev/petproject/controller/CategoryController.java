package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save-category")
    public String createCategory(@ModelAttribute("category") Category category, Model model) {
        log.info("Trying to save new  category: {}", category);

        categoryService.saveNewCategory(category);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAllCategories());
        log.info("New category was saved successfully: {}", category);
        return "redirect:/products/add?success=true";

    }

}
