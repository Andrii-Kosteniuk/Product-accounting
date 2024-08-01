package dev.petproject.service;

import dev.petproject.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if (authHeader == null || ! authHeader.startsWith("Bearer ")) {
            return;
        }
        jwtToken = authHeader.substring(7);
        var storedtoken = tokenRepository.findByToken(jwtToken).orElse(null);
        if (storedtoken != null) {
            storedtoken.setExpired(true);
            storedtoken.setRevoked(true);
            tokenRepository.save(storedtoken);
        }
    }
}
