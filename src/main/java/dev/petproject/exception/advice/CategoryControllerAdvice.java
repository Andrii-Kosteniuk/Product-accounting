package dev.petproject.exception.advice;

import dev.petproject.exception.CategoryAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CategoryControllerAdvice {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public String handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return "redirect:/products/add?error=true&message=" + ex.getMessage();
    }

}
