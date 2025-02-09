package dev.petproject.auth;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.dto.UserDTO;
import dev.petproject.exception.UserAlreadyExistsException;
import dev.petproject.repository.UserRepository;
import dev.petproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    public void register(UserDTO userDto, Role role) {
        User userToSave = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(role)
                .build();
        userRepository.findByEmail(userToSave.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistsException("User with email " + userToSave.getEmail() + " already exist");
        });
        userService.saveUser(userToSave);

    }
}