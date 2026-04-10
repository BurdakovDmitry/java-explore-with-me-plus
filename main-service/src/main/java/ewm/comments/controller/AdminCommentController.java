package ewm.comments.controller;

import ewm.comments.dto.AdminCommentSearchFilter;
import ewm.comments.dto.CommentFullDto;
import ewm.comments.dto.UpdateCommentStatusRequest;
import ewm.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> searchCommentFilter(@Valid @RequestBody AdminCommentSearchFilter filter) {
        log.info("GET/admin/comments: filter={}", filter);
        return commentService.searchComments(filter);
    }

    @PatchMapping("/{commentsId}")
    public CommentFullDto updateStatusComment(@PathVariable Long commentId,
                                              @Valid @RequestBody UpdateCommentStatusRequest request) {
        log.info("Patch/admin/comments/{}", commentId);
        return commentService.updateStatusComment(commentId, request);
    }

    @DeleteMapping("/{commentsId}")
    public void deleteCommentById(@PathVariable Long commentId) {
        log.info("Delete/admin/comments/{}", commentId);
        commentService.deleteComment(commentId);
    }
}
