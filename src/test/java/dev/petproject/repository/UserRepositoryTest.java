package dev.petproject.repository;

import dev.petproject.domain.Role;
import dev.petproject.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .password("password555*")
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    void givenSavedUser_whenFindById_thenUserByIdIsPresent() {

        //Given
        Integer id = user.getId();

        //When
        Optional<User> userById = userRepository.findById(id);

        //Then
        assertThat(userById).isNotEmpty();
        assertEquals(id, userById.get().getId());
    }

    @Test
    void givenSavedUser_whenFindByEmail_thenUserIsPresent() {

        //Given
        String email = "john.smith@gmail.com";

        //When
        Optional<User> userByEmail = userRepository.findByEmail(email);

        //Then
        assertThat(userByEmail).isPresent();
        assertThat(userByEmail.get().getEmail()).isEqualTo("john.smith@gmail.com");

    }
}
