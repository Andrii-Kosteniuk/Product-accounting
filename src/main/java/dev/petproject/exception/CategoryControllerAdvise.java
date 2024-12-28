package dev.petproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class CategoryControllerAdvise {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("edit");
        redirectAttributes.addAttribute("categoryEx", ex.getMessage());
        return modelAndView;
    }
}
