package dev.petproject.controller;

import dev.petproject.auth.JwtUtils;
import dev.petproject.repository.TokenRepository;
import dev.petproject.service.LogoutService;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogOutController.class)
class LogOutControllerTest {
    @MockBean
    LogoutService logoutService;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    TokenRepository tokenRepository;

    @MockBean
    UserService userService;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new LogOutController(logoutService)).build();
    }

    @Test
    void performLogout() throws Exception {

        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}