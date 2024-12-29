package dev.petproject.exception.advice;

import dev.petproject.exception.UserCanNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class UserControllerAdvice {

    @ExceptionHandler(UserCanNotBeDeletedException.class)
    public String handleUserCanNotBeDeletedException(UserCanNotBeDeletedException ex) {
        log.error(ex.getMessage());
        return "redirect:/users?error=true&message=" + ex.getMessage();
    }
}
