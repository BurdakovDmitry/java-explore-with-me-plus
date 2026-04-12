package ewm.comments.controller;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.CommentSearchParams;
import ewm.comments.service.CommentService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(required = false) String sort
    ) {
        CommentSearchParams params = new CommentSearchParams(text, events, rangeStart, rangeEnd, from, size, sort);
        return commentService.getPublishedComments(params);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        return commentService.getPublishedComment(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getPublishedCommentsByEvent(eventId);
    }
}