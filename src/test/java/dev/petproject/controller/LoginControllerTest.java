package dev.petproject.controller;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.auth.JwtUtils;
import dev.petproject.domain.Role;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.repository.TokenRepository;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.LogoutService;
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
    LogoutService logoutService;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    TokenRepository tokenRepository;

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
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(authenticationService)).build();
    }

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    void shouldShowRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRegisterUserWithNoErrors() throws Exception {
        UserDTO user = new UserDTO(
                1, "John", "Doe", "email@gmail.com", "555JohnDoe!", Role.USER);

        doNothing().when(authenticationService).register(any(UserDTO.class), any(Role.class));

        mockMvc.perform(post("/register")
                        .flashAttr("user", userDTO)
                        .param("role", user.getRole().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotRegisterUserAndThrowUserAlreadyExistsException() throws Exception {
        UserDTO userDTO = new UserDTO(1, "John", "Doe", "john@example.com", "password123", Role.USER);

        doThrow(new UserAlreadyExistsException("User already exists")).when(authenticationService).register(any(UserDTO.class), any(Role.class));

        mockMvc.perform(post("/register")
                        .flashAttr("user", userDTO)
                        .param("role", userDTO.getRole().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"));
    }

    @Test
    void shouldRegisterUserWithFieldError() throws Exception {
        String expected = "password must have * at least eight characters * include at least one number" +
                          "* include both lower and uppercase letters * include at least one special characters";

        when(result.hasErrors()).thenReturn(true);
        assertTrue(expected, result.hasErrors());

        mockMvc.perform(post("/register"))
                .andExpect(model().attributeHasErrors())
                .andExpect(view().name("registerPage"));
    }

    @Test
    void authenticate() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}