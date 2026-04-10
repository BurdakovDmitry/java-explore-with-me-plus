package ewm.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// eventId и userId из @PathVariable "/users/{userId}/events/{eventId}".
// Дата заполняется по дефолту на уровне БД
public record PostCommentDto(
        @NotBlank
        @Size(min = 20, max = 10000)
        String comment
) {}
