package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.domain.Role;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    BindingResult result;

    @MockBean
    Role role;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(authenticationService, userService)).build();
    }

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("login"));
    }

    @Test
    void shouldShowRegisterPage() throws Exception {
        mockMvc.perform(get("/auth/register/"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldSuccessfullyRegisterUser() throws Exception {
        UserDTO userToSave = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@mail.com")
                .password("johnDOE111")
                .build();

        doNothing().when(authenticationService).register(any(UserDTO.class), any(Role.class));

        mockMvc.perform(post("/auth/register/")

                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@mail.com")
                        .param("password", "johnDOE111")
                        .param("role", Role.USER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?success=true"));


        authenticationService.register(userToSave, role);

        verify(authenticationService, times(1)).register(userToSave, role);
    }

    @Test
    void shouldThrowUserAlreadyExistsException() throws Exception {
        UserDTO userDTO = new UserDTO("John", "Doe", "john@example.com", "password123", Role.USER);

        doThrow(new UserAlreadyExistsException("User already exists")).when(authenticationService).register(any(UserDTO.class), any(Role.class));

        mockMvc.perform(post("/auth/register/")
                        .flashAttr("user", userDTO)
                        .param("role", userDTO.getRole().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldRegisterUserWithFieldError() throws Exception {
        String expected = "Must be minimum 6 characters, include at least one letter, one number, and optionally special characters @$!%*?&";

        when(result.hasErrors()).thenReturn(true);
        assertTrue(expected, result.hasErrors());

        mockMvc.perform(post("/auth/register/"))
                .andExpect(model().attributeHasErrors())
                .andExpect(view().name("register"));
    }

}