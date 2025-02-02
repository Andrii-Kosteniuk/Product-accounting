package dev.petproject.exception.advice;

import dev.petproject.exception.EmptySymbolException;
import dev.petproject.exception.ProductAlreadyExistsException;
import dev.petproject.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class ProductControllerAdvice {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public String handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return "redirect:/products/all?error=true&message=" + ex.getMessage();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFoundException(ProductNotFoundException ex) {
        log.error(ex.getMessage());
        return "redirect:/404?error=true&message=" + ex.getMessage();
    }

    @ExceptionHandler(EmptySymbolException.class)
    public ModelAndView handleEmptySymbolException(EmptySymbolException ex) {
        log.error(ex.getMessage());
        return new ModelAndView("products", "errorSearch", ex.getMessage());
    }

}
