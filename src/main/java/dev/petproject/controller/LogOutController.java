package dev.petproject.controller;

import dev.petproject.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
public class LogOutController {

    private final LogoutService logoutService;


    @PostMapping("/logout")
    public String performLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)  {
        logoutService.logout(request, response, authentication);
        log.info("Logged out successfully");
        return "redirect:/login";
    }
}
