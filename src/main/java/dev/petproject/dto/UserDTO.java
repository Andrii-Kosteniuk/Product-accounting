package dev.petproject.dto;

import dev.petproject.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotEmpty(message = "User first name can not be empty")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Name must start with a capital letter followed by one or more lowercase letters")
    private String firstName;


    @NotEmpty(message = "User last name can not be empty")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Last name must start with a capital letter followed by one or more lowercase letters")
    private String lastName;


    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$",
            message = """
                    Email can contain one character from the English alphabet (both cases),
                    digits, and also special characters like "+", "_", "." and, "-" before the @ symbol""")
    private String email;

    @Pattern(
            regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}",
            message = "Must be minimum 6 characters, include at least one letter, one number, and optionally special characters @$!%*?&"
    )
    private String password;

    private Role role;

}
