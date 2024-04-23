package dev.petproject.controller;

import dev.petproject.auth.AuthenticationRequest;

import dev.petproject.dto.UserDto;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class LoginPageController {
    private final AuthenticationService service;

    @GetMapping("/login")
    public String showLoginPage() {
        return "log-in";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "reg-ister";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute(name = "user") UserDto userDto, BindingResult result) {

        if (result.hasErrors()) {
            return "reg-ister";
        }
        service.register(userDto);
        return "redirect:/register?success";
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute AuthenticationRequest request) {

        service.authenticate(request);
        return "index";
    }
}
