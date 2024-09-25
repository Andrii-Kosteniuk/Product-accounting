package dev.petproject.service;

import dev.petproject.domain.Token;
import dev.petproject.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    TokenRepository tokenRepository;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @InjectMocks
    LogoutService logoutService;

    @BeforeEach
    void setUp() {
        logoutService = new LogoutService(tokenRepository);
    }

    @Test
    void shouldNotLogoutWhenAuthorizationHeaderIsNull() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        logoutService.logout(request, response, null);

        // Assert
        verify(tokenRepository, never()).findByToken(any());
    }

    @Test
    void shouldNotLogoutWhenAuthorizationHeaderDoesNotStartWithBearer() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        // Act
        logoutService.logout(request, response, null);

        // Assert
        verify(tokenRepository, never()).findByToken(any());
    }

    @Test
    void shouldLogoutWhenValidBearerTokenProvidedAndTokenExists() {
        // Arrange
        String jwtToken = "validJwtToken";
        Token token = new Token();
        token.setExpired(false);
        token.setRevoked(false);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(token));

        // Act
        logoutService.logout(request, response, null);

        // Assert
        verify(tokenRepository).findByToken(jwtToken);
        verify(tokenRepository).save(token);
        assert(token.isExpired());
        assert(token.isRevoked());
    }

    @Test
    void shouldNotLogoutWhenTokenIsNotFound() {
        // Arrange
        String jwtToken = "validJwtToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.empty());

        // Act
        logoutService.logout(request, response, null);

        // Assert
        verify(tokenRepository).findByToken(jwtToken);
        verify(tokenRepository, never()).save(any());
    }
}