package dev.petproject.exception.advice;

import dev.petproject.dto.ChangePasswordDTO;
import dev.petproject.exception.PasswordException;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.exception.UserCanNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class UserControllerAdvice {

    @ExceptionHandler(UserCanNotBeDeletedException.class)
    public String handleUserCanNotBeDeletedException(UserCanNotBeDeletedException ex) {
        log.error(ex.getMessage());
        return "redirect:/users?error=true&message=" + ex.getMessage();
    }

    @ExceptionHandler(PasswordException.class)
    public ModelAndView handlePasswordException(PasswordException ex, Model model) {
        log.error("Password is incorrect");
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());

        return new ModelAndView("change-password", "errorChangePassword", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("UserAlreadyExistsException occurred when trying to save new user");

        return "redirect:/auth/register?error=true";
    }



}
