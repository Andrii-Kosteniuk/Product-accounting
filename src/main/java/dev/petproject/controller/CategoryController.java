package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save-category")
    public String createCategory(@Valid @ModelAttribute("category") Category category, BindingResult result, HttpSession session,  Model model) {
        log.info("Trying to save new  category: {}", category);

        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("product", session.getAttribute("product"));

        if (result.hasErrors()) {
            log.error(result.getAllErrors().toString());
            model.addAttribute("fieldErrorMessage", "The category name you were provided is incorrect... Try to provide another one!");
            return "edit";
        }
        categoryService.saveNewCategory(category);
        log.info("New category was saved successfully: {}", category);
        return "redirect:/products/add?success=true";

    }

}
