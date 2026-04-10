package ewm.compilation.dto;

import java.util.List;

public record UpdateCompilationDto(
        List<Long> events,

        String title,

        Boolean pinned
) {
}
