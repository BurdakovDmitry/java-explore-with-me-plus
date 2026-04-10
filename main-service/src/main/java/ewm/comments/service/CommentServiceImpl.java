package ewm.comments.service;

import com.querydsl.core.BooleanBuilder;
import ewm.comments.dto.AdminCommentSearchFilter;
import ewm.comments.dto.CommentFullDto;
import ewm.comments.dto.UpdateCommentStatusRequest;
import ewm.comments.mapper.CommentMapper;
import ewm.comments.model.Comment;
import ewm.comments.model.CommentStatus;
import ewm.comments.model.QComment;
import ewm.comments.repository.CommentRepository;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public List<CommentFullDto> searchComments(AdminCommentSearchFilter filter) {
        log.info("Admin search comment with filter: {}", filter);

        if (filter.rangeStart() != null && filter.rangeEnd() != null
                && filter.rangeStart().isAfter(filter.rangeEnd())) {
            throw new ValidationException("rangeEnd cannot be earlier than rangeStart");
        }

        QComment qComment = QComment.comment1;
        BooleanBuilder predicate = new BooleanBuilder();

        Pageable pageable = PageRequest.of(filter.from() / filter.size(), filter.size());

        if (filter.text() != null && !filter.text().isBlank()) {
            predicate.and(qComment.comment.containsIgnoreCase(filter.text()));
        }

        if (filter.users() != null && !filter.users().isEmpty()) {
            predicate.and(qComment.author.id.in(filter.users()));
        }

        if (filter.events() != null && !filter.events().isEmpty()) {
            predicate.and(qComment.event.id.in(filter.events()));
        }

        if (filter.rangeStart() != null) {
            predicate.and(qComment.createdOn.goe(filter.rangeStart()));
        }

        if (filter.rangeEnd() != null) {
            predicate.and(qComment.createdOn.loe(filter.rangeEnd()));
        }

        if (filter.status() != null) {
            predicate.and(qComment.status.eq(filter.status()));
        }

        List<Comment> comments = commentRepository.findAll(predicate, pageable).getContent();

        if (comments.isEmpty()) {
            return List.of();
        }

        return commentMapper.toFullDtoList(comments);
    }

    @Override
    @Transactional
    public CommentFullDto updateStatusComment(Long commentId, UpdateCommentStatusRequest request) {
        log.info("Admin update comment id={} with status={}", commentId, request.status());

        Comment comment = existsComment(commentId);
        CommentStatus newStatus = request.status();

        if (newStatus == CommentStatus.PUBLISHED && comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("You can only publish a comment with status: PENDING");
        }
        if (newStatus == CommentStatus.REJECTED && comment.getStatus() == CommentStatus.PUBLISHED) {
            throw new ConflictException("You cant reject already published comments");
        }

        comment.setStatus(newStatus);
        return commentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Admin delete comment id={}", commentId);
        Comment comment = existsComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment existsComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id= " + commentId + " was not found"));
    }
}
