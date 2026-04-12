package ewm.comments.service;

import ewm.comments.dto.CommentDto;

import ewm.comments.dto.PostCommentParam;
import ewm.comments.dto.UpdateCommentParam;

import java.util.List;

public interface CommentService {
    CommentDto create(PostCommentParam postCommentParam);

    CommentDto update(UpdateCommentParam updCommentParam);

    void delete(Long userId, Long commentId);

     List<CommentDto> findAllByAuthor(Long userId);

    CommentDto findByIdAndAuthor(Long userId, Long commentId);

    List<CommentDto> findAllByEventAndAuthor(Long userId, Long eventId);
}
