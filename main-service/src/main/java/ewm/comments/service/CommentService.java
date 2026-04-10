package ewm.comments.service;

import ewm.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getPublishedComments(String text, List<Long> events, String rangeStart, String rangeEnd, int from, int size, String sort);

    CommentDto getPublishedComment(Long commentId);

    List<CommentDto> getPublishedCommentsByEvent(Long eventId);
}
