package dev.petproject.dto;

import dev.petproject.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class UserDTO {

    private final String errorPasswordMessage =
            """
                    password must have
                    * at least eight characters
                    * include at least one number
                    * include both lower and uppercase letters
                    * include at least one special characters""";

    private final String errorEmailMessage =
            """
                    email can contain
                    * one character from the English alphabet (both cases), +
                    digits, "+", "_", "." and, "-" before the @ symbol""";

    private Integer id;
    @Size(min = 3, max = 15)
    @NotEmpty(message = "User's name cannot be empty.")
    private String firstName;
    @Size(min = 3, max = 15)
    @NotEmpty(message = "User's name cannot be empty.")
    private String lastName;
    @NotEmpty(message = "User's email cannot be empty.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = errorEmailMessage)
    private String email;
    @NotNull
//    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
//            message = errorPasswordMessage)
    private String password;
    @NotNull
    private Role role;

}
