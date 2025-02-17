package dev.petproject.security;

import dev.petproject.auth.AuthenticationService;
import dev.petproject.repository.ProductRepository;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;
    @Qualifier("userDetailsService")
    @Autowired
    private UserDetailsService userDetailsService;


    @Test
    @WithMockUser(authorities = "USER")
    void givenUserRole_whenAccessAdminPage_thenForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenNoUser_whenAccessLoginPage_thenSuccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserNameNotFoundException() {

        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testPasswordEncoderBeanIsNotNull() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should not be null");
    }

    @Test
    void testPasswordEncryption() {
        String rawPassword = "securePassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword, "Encoded password should be different from the raw password");
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "Password should match after encoding");
    }

    @Test
    void testInvalidPasswordDoesNotMatch() {
        String rawPassword = "securePassword123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword), "Wrong password should not match");
    }
}
