package ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CompilationPostDto(
        List<Long> events,

        @NotBlank
        String title,

        Boolean pinned
) {
}
