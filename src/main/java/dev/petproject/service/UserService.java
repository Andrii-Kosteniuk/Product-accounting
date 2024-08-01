package dev.petproject.service;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.repository.TokenRepository;
import dev.petproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public User findUserById(Integer id) {
        Optional<User> user = userRepository.findUserById(id);
        return user.orElse(null);
    }

    public List<User> findAllRegisteredUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Integer id) {
        User user = this.findUserById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String registeredUserEmail = userDetails.getUsername();

        if (! user.getEmail().equals(registeredUserEmail)
            && ! user.getRole().equals(Role.ADMIN)) {
            tokenRepository.deleteTokenByUserId(id);
        } else throw new UserCanNotBeDeletedException("You can not delete user with" + registeredUserEmail);

    }
}
