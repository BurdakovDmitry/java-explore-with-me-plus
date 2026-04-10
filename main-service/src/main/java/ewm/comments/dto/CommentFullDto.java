package ewm.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.comments.model.CommentStatus;

import java.time.LocalDateTime;

public record CommentFullDto(
        Long id,
        String comment,
        CommentStatus status,
        Long eventId,
        Long authorId,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn
) {
}