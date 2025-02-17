package dev.petproject.security;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.controller.HomeController;
import dev.petproject.controller.LoginController;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({SecurityFilterChainConfig.class, LoginController.class, HomeController.class})
@AutoConfigureMockMvc
class SecurityFilterChainConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private AuthenticationService service;

    @MockBean
    private UserService userService;


    @Test
    void whenAccessingPublicEndpoints_thenShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenAccessingAuthenticatedEndpoints_thenShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessingAuthenticatedEndpointsWithoutLogin_thenShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenLoggingOut_thenShouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout=true"));
    }

}