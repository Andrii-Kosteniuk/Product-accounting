package dev.petproject.controller;

import dev.petproject.dto.ChangePasswordDTO;
import dev.petproject.exception.PasswordException;
import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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
        String userEmail = userService.findUserById(id).getEmail();
        try {
            userService.deleteUser(id);
            log.info("User with ID: {} deleted successfully", id);
            model.addAttribute("userDeletedSuccessfully", "The user with email " + userEmail + " has been deleted successfully");
            return "users";
        } catch (UserCanNotBeDeletedException ex) {
            log.warn("Failed to delete user with ID: {}. Reason: {}", id, ex.getMessage());
            model.addAttribute("userCanNotBeDeleteException", "You can not delete user with email " + userEmail);
            return "users";
        }
    }

    @GetMapping("/users/change-password")
    public String showFormToChangePassword(Model model) {
        log.info("Accessing change password form.");
        model.addAttribute("errorChangePassword");
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());

        return "change-password";
    }

    @PostMapping("/users/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
                                 BindingResult result,
                                 Principal principal, Model model) {
        log.info("Received change password request for user: {}", principal.getName());

        if (result.hasErrors()) {
            log.warn("Validation errors occurred: {}", result.getAllErrors());
            return "change-password";
        }

        String userName = principal.getName();
        log.info("Processing password change request for user: {}", userName);

        var user = userService.loadUserByUsername(userName);
        log.debug("User found: {}", user.getId());

        if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {

            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(user);

            log.info("Password was changed successfully for user {} ", userName);
            model.addAttribute("successChangePassword", "Password was changed successfully");
            return "change-password";

        } else {
            log.error("Incorrect old password for user {}", userName);
            throw new PasswordException("The old password you provided is incorrect. Please try again :-)");
        }
    }

}
