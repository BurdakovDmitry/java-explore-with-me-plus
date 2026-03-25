package ewm.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(HttpStatus status, String reason, String message, LocalDateTime timestamp) {
}
