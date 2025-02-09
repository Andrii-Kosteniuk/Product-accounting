package dev.petproject.service;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import dev.petproject.exception.UserCanNotBeDeletedException;
import dev.petproject.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = List.of(
                new User(1, "John", "Smith", "john.smith@gmail.com", "password_One", Role.USER),
                new User(2, "Sara", "Bird", "sara_bird@gmail.com", "password_Two", Role.USER),
                new User(3, "Adam", "Sab", "adam.sab@gmail.com", "password_Three", Role.ADMIN)
        );

    }

    @Test
    void givenUserID_whenFindUserById_shouldReturnUser() {
        // Given
        Integer id = 1;
        User user = users.get(id);

        given(userRepository.findUserById(id)).willReturn(Optional.of(user));

        // When
        User resulUser = userService.findUserById(id);

        // Then
        assertNotNull("User is not present", resulUser);
        assertEquals("User is not  the same", resulUser, user);
    }

    @Test
    void shouldReturn_allRegisteredUsers() {
        when(userRepository.findAll()).thenReturn(users);

        List<User> allRegisteredUsers = userService.findAllRegisteredUsers();

        assertFalse(allRegisteredUsers.isEmpty());
        assertEquals("Lists have differences", users, allRegisteredUsers);
        assertEquals("There are two different lists", allRegisteredUsers.size(), users.size());
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = new User(1, "John", "Smith", "john.smith@gmail.com", "password_One", Role.USER);
        given(userRepository.findUserById(1)).willReturn(Optional.of(user));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal())
                .willReturn(new org.springframework.security.core.userdetails.User("adam.sab@gmail.com", "password_Three", new ArrayList<>()));


        SecurityContextHolder.setContext(securityContext);

        // When
        userService.deleteUser(1);

        List<User> usersAfterDeletes = userService.findAllRegisteredUsers();

        // Then
        assertNotEquals(usersAfterDeletes.size(), users.size(), "List of users did not change");
    }

    @Test
    void shouldThrowUserCanNotBeDeletedException_WhenDeletingOwnAccount() {
        // Given
        User user = new User(1, "John", "Smith", "john.smith@gmail.com", "password_One", Role.USER);
        given(userRepository.findUserById(1)).willReturn(Optional.of(user));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal())
                .willReturn(new org.springframework.security.core.userdetails.User("john.smith@gmail.com", "password_One", new ArrayList<>()));

        SecurityContextHolder.setContext(securityContext);

        // When
        UserCanNotBeDeletedException exception = assertThrows(UserCanNotBeDeletedException.class, () -> userService.deleteUser(1));

        // Then
        Assertions.assertEquals("You can not delete user with email john.smith@gmail.com", exception.getMessage());

    }

    @Test
    void shouldThrowUserCanNotBeDeletedException_WhenDeletingAdminUser() {
        // Given
        User user = new User(3, "Adam", "Sab", "adam.sab@gmail.com", "password_Three", Role.ADMIN);
        given(userRepository.findUserById(3)).willReturn(Optional.of(user));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal())
                .willReturn(new org.springframework.security.core.userdetails.User("john.smith@gmail.com", "password_One", new ArrayList<>()));

        SecurityContextHolder.setContext(securityContext);

        // When
        UserCanNotBeDeletedException exception = assertThrows(UserCanNotBeDeletedException.class, () -> userService.deleteUser(3));

        // Then
        Assertions.assertEquals("You can not delete user with email john.smith@gmail.com", exception.getMessage());
    }
}