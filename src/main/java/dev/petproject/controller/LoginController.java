package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final AuthenticationService service;

    @GetMapping("/login")
    public String showLoginPage() {
        log.info("Accessed the login page");
        return "loginPage";
    }

    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("user") UserDTO user, Model model) {
        log.info("Accessed the registration page");
        model.addAttribute("user", user);
        return "registerPage";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDTO user, BindingResult result, Role role, Model model) throws MessagingException {
        log.info("Attempting to register a new user with email: {}", user.getEmail());

        if (result.hasErrors()) {
            log.warn("Registration form contains errors for user: {}", user.getEmail());
            return "registerPage";
        }

        try {
            service.register(user, role);
            log.info("User successfully registered with email: {}", user.getEmail());
            return "redirect:/login?success";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("errorRegister", e);
            log.warn("Registration failed for user: {}. Reason: {}", user.getEmail(), e.getMessage());
            return "registerPage";
        }
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute User user) {
        log.info("Attempting to authenticate user with email: {}", user.getEmail());
        service.authenticate(user);
        log.info("User successfully authenticated with email: {}", user.getEmail());
        return "index";
    }
}
