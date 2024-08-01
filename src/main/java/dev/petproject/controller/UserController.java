package dev.petproject.controller;

import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAllRegisteredUsers());
        return "users";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable(value = "id") Integer id, Model model) {

        try {

            userService.deleteUser(id);
            return "redirect:/users?success";
        } catch (UserCanNotBeDeletedException ex) {
            model.addAttribute("userDeleteException", ex.getMessage());
        }
        return "redirect:/users?error";

    }

}
