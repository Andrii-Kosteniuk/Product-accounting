package dev.petproject.controller;


import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {


    @MockBean
    Authentication authentication;
    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, passwordEncoder, userRepository)).build();

        users = List.of(
                new User(1, "J", "M", "jM@gmail.com", "11111", Role.USER),
                new User(2, "A", "O", "AO@gmail.com", "22222", Role.ADMIN)
        );

        var principal = users.get(1);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "AO@gmail.com", password = "22222", roles = "ADMIN")
    void shouldPerformGetAllUsersAndReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"));

    }

    @Test
    @WithMockUser(username = "user", password = "Password5", roles = "ADMIN")
    void testDeleteUserSuccess() throws Exception {
        when(userService.findUserById(1)).thenReturn(users.get(0));
        doNothing().when(userRepository).deleteById(1);

        mockMvc.perform(get("/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users?success"));
    }


}