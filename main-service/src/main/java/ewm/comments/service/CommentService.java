package ewm.comments.service;

import ewm.comments.dto.AdminCommentSearchFilter;
import ewm.comments.dto.CommentFullDto;
import ewm.comments.dto.UpdateCommentStatusRequest;

import java.util.List;

public interface CommentService {
    List<CommentFullDto> searchComments(AdminCommentSearchFilter filter);

    CommentFullDto updateStatusComment(Long commentId, UpdateCommentStatusRequest status);

    void deleteComment(Long commentId);
}
