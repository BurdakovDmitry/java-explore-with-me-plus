package ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserPostDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String name) {
}
