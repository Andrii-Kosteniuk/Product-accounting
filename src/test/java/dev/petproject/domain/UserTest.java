package dev.petproject.domain;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Constructor;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    @Test
    void testUserDetailsMethods() {
        User user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword")
                .role(Role.USER)
                .build();

        assertEquals("securePassword", user.getPassword());
        assertEquals("john.doe@example.com", user.getUsername());
        assertEquals(1, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("USER", authorities.iterator().next().getAuthority());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());

        Class<User> userClass = User.class;
        Constructor<?>[] constructors = userClass.getConstructors();
        assertTrue(constructors.length > 1);
    }
}