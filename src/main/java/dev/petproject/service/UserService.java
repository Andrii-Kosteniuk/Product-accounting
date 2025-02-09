package dev.petproject.service;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Integer id) {
        Optional<User> user = userRepository.findUserById(id);
        return user.orElse(null);
    }

    @CacheEvict(value = "users", allEntries = true)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Cacheable("users")
    public List<User> findAllRegisteredUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Integer id) {
        User user = this.findUserById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String registeredUserEmail = userDetails.getUsername();

        if (! user.getEmail().equals(registeredUserEmail)
            && ! user.getRole().equals(Role.ADMIN)) {
            userRepository.delete(user);
        } else {
            throw new UserCanNotBeDeletedException("You can not delete user with email " + registeredUserEmail);
        }
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
