package dev.petproject.exception.advice;

import dev.petproject.domain.Category;
import dev.petproject.exception.CategoryAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class CategoryControllerAdvice {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ModelAndView handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex, Model model, HttpSession session) {
        log.error(ex.getMessage());

        model.addAttribute("product", session.getAttribute("product"));
        model.addAttribute("category", session.getAttribute("category"));
        model.addAttribute("category", new Category());
        model.addAttribute("categories", session.getAttribute("categories"));
        return new ModelAndView("edit", "errorCreateNewCategory", ex.getMessage());
    }

}
