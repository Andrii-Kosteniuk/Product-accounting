package dev.petproject.controller;


import dev.petproject.auth.JwtUtils;
import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.repository.TokenRepository;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {


    @MockBean
    UserService userService;
    @MockBean
    JwtUtils jwtUtils;
    @MockBean
    TokenRepository tokenRepository;
    @MockBean
    Authentication authentication;
    @Autowired
    private MockMvc mockMvc;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = List.of(
                new User(1, "J", "M", "jM@gmail.com", "11111", Role.USER),
                new User(2, "A", "O", "AO@gmail.com", "22222", Role.ADMIN)
        );
    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void shouldPerformGetAllUsersAndReturnStatusIsOk() throws Exception {

        when(userService.findAllRegisteredUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("users"));

    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void testDeleteUserSuccess() throws Exception {
        when(userService.findAllRegisteredUsers()).thenReturn(users);

        mockMvc.perform(get("/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users?success"));
    }

}