package ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record NewCompilationDto(
        List<Long> events,

        @NotBlank
        String title,

        Boolean pinned
) {
}
