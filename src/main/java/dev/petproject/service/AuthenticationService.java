package dev.petproject.service;

import dev.petproject.auth.AuthenticationRequest;
import dev.petproject.auth.AuthenticationResponse;
import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.dto.UserDto;
import dev.petproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public void register(UserDto userDto) {
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.USER)
                .build();
        if (userRepository.findByEmail(user.getEmail()).isEmpty())
            userRepository.save(user);
        var jwtToken = jwtUtils.generateToken(user);
        AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);
        AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
