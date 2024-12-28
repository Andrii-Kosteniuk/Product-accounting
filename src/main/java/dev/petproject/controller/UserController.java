package dev.petproject.controller;

import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllUsers(Model model) {
        log.info("Fetching all registered users");
        model.addAttribute("users", userService.findAllRegisteredUsers());
        return "users";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable(value = "id") Integer id, Model model) {
        log.info("Attempting to delete user with ID: {}", id);
        model.addAttribute("users", userService.findAllRegisteredUsers());
        try {
            userService.deleteUser(id);
            log.info("User with ID: {} deleted successfully", id);
            return "redirect:/users?success";
        } catch (UserCanNotBeDeletedException ex) {
            log.warn("Failed to delete user with ID: {}. Reason: {}", id, ex.getMessage());
            model.addAttribute("userCanNotBeDeleteException", ex);
            return "users";
        }
    }
}
