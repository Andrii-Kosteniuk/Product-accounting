package dev.petproject.controller;


import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.dto.ChangePasswordDTO;
import dev.petproject.exception.PasswordException;
import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {


    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BindingResult result;

    @Autowired
    private MockMvc mockMvc;

    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, passwordEncoder, userRepository)).build();

        users = List.of(
                new User(1, "J", "M", "jM@gmail.com", "aaAA11", Role.USER),
                new User(2, "A", "O", "AO@gmail.com", "22222", Role.ADMIN)
        );

        var principal = users.get(0);
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


    @Test
    @WithMockUser(username = "jM@gmail.com", roles = "USER")
    void whenUserClickOnChangePassword_RedirectOnChangePasswordPage() throws Exception {
        mockMvc.perform(get("/users/change-password/"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"));
    }

    @Test
    @WithMockUser(username = "jM@gmail.com", roles = "USER")
    void testChangePassword() throws Exception {
        User user = users.get(0);
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(
                "aaAA11", "bbBBB22");

        when(userService.loadUserByUsername("jM@gmail.com")).thenReturn(user);
        System.out.println(userService.loadUserByUsername("jM@gmail.com"));
        when(passwordEncoder.matches("aaAA11", user.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/change-password/")
                        .flashAttr("changePasswordDTO", changePasswordDTO)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("successChangePassword"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "jM@gmail.com", roles = "USER")
    void testFieldsErrorWhenChangePassword() throws Exception {
        when(result.hasErrors()).thenReturn(true);

        mockMvc.perform(post("/users/change-password/")
                        .param("oldPassword", "aaAA11")
                        .param("newPassword", "b")
                        .with(csrf()))
                .andExpect(view().name("change-password"));

        Assertions.assertTrue(result.hasErrors());

    }

    @Test
    @WithMockUser(username = "AO@gmail.com", roles = "ADMIN")
    void testUserCanNotBeDeletedException() throws Exception {
        // Given
        doNothing().when(userRepository).deleteById(1);
        doThrow(new UserCanNotBeDeletedException("You can not delete this user"))
                .when(userService).deleteUser(1);

        // When
        mockMvc.perform(get("/delete/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("userCanNotBeDeleteException"))
                .andExpect(model().attribute("userCanNotBeDeleteException",
                        "You can not delete this user"));

        // Then
        verifyNoInteractions(userRepository);
    }

    @Test
    @WithMockUser(username = "AO@gmail.com", roles = "ADMIN")
    void testPasswordException() throws Exception {
        // Given
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        doThrow(PasswordException.class)
                .when(passwordEncoder).matches(anyString(), anyString());

        // When
        mockMvc.perform(get("/users/change-password/"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"));

        // Then
        verifyNoInteractions(userRepository);
    }

}