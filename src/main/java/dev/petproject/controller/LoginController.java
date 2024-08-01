package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.exception.UserAlreadyExistException;
import dev.petproject.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationService service;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("user") UserDTO user, Model model) {
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDTO user, BindingResult result, Role role, Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            service.register(user, role);
            return "redirect:/login?success";
        } catch (UserAlreadyExistException e) {
            model.addAttribute("errorRegisterMessage", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute User user) {

        service.authenticate(user);
        return "index";
    }
}
