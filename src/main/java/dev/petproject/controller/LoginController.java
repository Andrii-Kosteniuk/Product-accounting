package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.domain.Role;
import dev.petproject.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class LoginController {
    private final AuthenticationService service;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        log.info("Accessed the login page");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("userDTO") UserDTO userDTO, Model model) {
        log.info("Accessed the registration page");
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("errorRegister");
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Role role) {
        log.info("Attempting to register a new user with email: {}", userDTO.getEmail());

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error ->
                    log.error("Field error in field '{}': {}", error.getField(), error.getDefaultMessage()));

            return "register";
        }

        service.register(userDTO, role);
        log.info("User successfully registered with email: {}", userDTO.getEmail());
        return "redirect:/auth/login?success=true";

    }

 }
