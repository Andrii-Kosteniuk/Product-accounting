package dev.petproject.exception.advice;

import dev.petproject.exception.UserCanNotBeDeletedException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
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


    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredJwtException(ExpiredJwtException ex, Model model) {
        log.error("Exception {} related to token expiration time", ex.getMessage());
        model.addAttribute("tokenExpiredException", true);
        model.addAttribute("message", "You need to authenticate first");

        return "redirect:/login";
    }
}
