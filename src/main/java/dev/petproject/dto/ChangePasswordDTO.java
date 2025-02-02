package dev.petproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDTO {

    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    @Pattern(
            regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}",
            message = "Must be minimum 6 characters, include at least one letter, one number, and optionally special characters @$!%*?&"
    )
    private String newPassword;
}
