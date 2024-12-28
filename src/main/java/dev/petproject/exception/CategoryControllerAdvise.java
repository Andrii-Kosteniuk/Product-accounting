package dev.petproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class CategoryControllerAdvise {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public String handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return "redirect:/products/add?error=true&message=" + ex.getMessage();
    }
}
