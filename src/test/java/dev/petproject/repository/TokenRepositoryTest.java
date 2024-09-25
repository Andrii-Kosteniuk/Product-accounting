package dev.petproject.repository;

import dev.petproject.domain.Role;
import dev.petproject.domain.Token;
import dev.petproject.domain.TokenType;
import dev.petproject.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenRepositoryTest {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private Token testToken;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .password("passworD555")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        testToken = Token.builder()
                .user(user)
                .token("Token_1")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(testToken);
    }

    @Test
    void givenUserId_whenDeleteTokenByUserId_thenUserWithSuchIdShouldBeDeleted() {

        //Given
        Integer userId = user.getId();

        //When
        tokenRepository.deleteTokenByUserId(userId);
        Optional<Token> foundToken = tokenRepository.findById(testToken.getId());

        //Then
        assertThat(foundToken).isEmpty();
    }

    @Test
    void givenToken_whenFindByToken_shouldReturnUserName() {
        //Given
        Token token = testToken;

        //When
        Optional<Token> foundToken = tokenRepository.findByToken(token.getToken());

        //Then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("Token_1");
    }
}