package ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record NewCompilationDto(
        List<Long> events,

        @NotBlank
        @Length(min = 1, max = 50, message = "Длина заголовка не должна превышать 50 символов")
        String title,

        Boolean pinned
) {
}
