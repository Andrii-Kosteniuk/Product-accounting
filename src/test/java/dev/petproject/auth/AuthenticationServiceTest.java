package dev.petproject.auth;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, userService);

        user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .password("password")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldRegisterNewUser() {
       when(userService.saveUser(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals(savedUser.getEmail(), "john.smith@gmail.com");

        verify(userService, times(1)).saveUser(any(User.class));
    }


    @Test
    void whenRegisterUser_shouldThrowUserAlreadyExistsException() {
        UserDTO userDTO = new UserDTO("John", "Smith", "john.smith@gmail.com", "111aaAA*", Role.USER);
        User existingUser = user;

        when(userRepository.findByEmail("john.smith@gmail.com")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(userDTO, Role.USER));
        verify(userRepository, never()).save(any(User.class));
    }

}