package dev.petproject.auth;

import dev.petproject.domain.Role;
import dev.petproject.domain.Token;
import dev.petproject.domain.TokenType;
import dev.petproject.domain.User;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.repository.TokenRepository;
import dev.petproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    TokenRepository tokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService authenticationService;

    User user;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userRepository, tokenRepository, passwordEncoder, jwtUtils, authenticationManager);

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
        String email = "john.smith@gmail.com";

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userRepository.save(user);
        Optional<User> userByEmail = userRepository.findByEmail(email);

        assertTrue(userByEmail.isPresent());
        assertSame(userByEmail.get(), user);
        assertThat(userByEmail).isPresent();

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void authenticate() {
        String email = user.getEmail();
        String password = user.getPassword();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(email, password));

    }

    @Test
    void whenRegisterUser_shouldThrowUserAlreadyExistsException() {
        UserDTO userDTO = new UserDTO(1, "John", "Smith", "john.smith@gmail.com", "111aaAA*", Role.USER);
        User existingUser = user;

        when(userRepository.findByEmail("john.smith@gmail.com")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(userDTO, Role.USER));
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testBuildGenerateToken() {
        Token token = Token.builder()
                .id(1)
                .token("token")
                .tokenType(TokenType.BEARER)
                .build();


        when(jwtUtils.generateToken(user)).thenReturn(token.getToken());

        String generatedToken = jwtUtils.generateToken(user);

        assertFalse(generatedToken.isEmpty());
        assertEquals(generatedToken, token.getToken());

    }

    @Test
    void testSaveUserToken() {
        Token token = Token.builder()
                .id(1)
                .user(user)
                .token("token")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        when(tokenRepository.save(token)).thenReturn(token);
        tokenRepository.save(token);

        when(tokenRepository.findAllValidTokenByUserId(user.getId())).thenReturn(new ArrayList<>(List.of(token)));
        List<Token> allValidTokenByUserId = tokenRepository.findAllValidTokenByUserId(user.getId());

        assertThat(allValidTokenByUserId).hasSize(1);
        assertThat(allValidTokenByUserId.contains(token)).isTrue();
    }

}