package dev.petproject.auth;

import dev.petproject.domain.Role;
import dev.petproject.domain.Token;
import dev.petproject.domain.TokenType;
import dev.petproject.domain.User;
import dev.petproject.exception.UserAlreadyExistException;
import dev.petproject.repository.TokenRepository;
import dev.petproject.repository.UserRepository;
import dev.petproject.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    private static void buildToken(String jwtToken) {
        AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @ExceptionHandler
    public void register(UserDTO user, Role role) {
        User userToSave = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(role)
                .build();
        userRepository.findByEmail(userToSave.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistException("User with email" + userToSave.getEmail() + "already exist");
        });
        userRepository.save(userToSave);

        var jwtToken = jwtUtils.generateToken(userToSave);
        saveUserToken(userToSave, jwtToken);
        buildToken(jwtToken);

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void authenticate(User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        var userTOAuthenticate = userRepository.findByEmail(user.getEmail()).orElseThrow();
        var jwtToken = jwtUtils.generateToken(userTOAuthenticate);
        revokeAllUsersToken(userTOAuthenticate);
        saveUserToken(userTOAuthenticate, jwtToken);
        buildToken(jwtToken);

    }

    private void revokeAllUsersToken(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
                    t.setExpired(true);
                    t.setRevoked(true);
                }
        );
        tokenRepository.saveAll(validUserTokens);
    }
}
