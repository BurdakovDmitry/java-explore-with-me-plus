package ewm.comments.dto;

import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record CommentSearchParams(
        String text,
        List<Long> events,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rangeStart,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rangeEnd,

        @Min(0) Integer from,
        @Min(1) Integer size,
        String sort
) {
    public CommentSearchParams {
        if (from == null) from = 0;
        if (size == null) size = 10;
        if (sort == null) sort = "desc";
    }
}