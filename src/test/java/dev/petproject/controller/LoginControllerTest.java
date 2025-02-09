package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.domain.Role;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
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
    Model model;

    @MockBean
    UserDTO userDTO;

    @MockBean
    BindingResult result;

    @MockBean
    Role role;

    @InjectMocks
    LoginController loginController;

    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(authenticationService, userService)).build();
    }

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login/"))
                .andExpect(status().isOk())
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
        UserDTO user = new UserDTO("John", "Doe", "email@gmail.com", "555JohnDoe!", Role.USER);

        doNothing().when(authenticationService).register(any(UserDTO.class), any(Role.class));

        mockMvc.perform(post("/auth/register/")
                        .flashAttr("user", userDTO)
                        .param("role", user.getRole().toString()))
                .andExpect(status().isOk());
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