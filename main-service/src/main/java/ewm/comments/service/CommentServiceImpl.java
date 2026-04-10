package ewm.comments.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.comments.dto.AdminCommentSearchFilter;
import ewm.comments.dto.CommentDto;
import ewm.comments.dto.PostCommentParam;
import ewm.comments.dto.UpdateCommentParam;
import ewm.comments.dto.UpdateCommentStatusRequest;
import ewm.comments.mapper.CommentMapper;
import ewm.comments.model.Comment;
import ewm.comments.model.CommentStatus;
import ewm.comments.model.QComment;
import ewm.comments.repository.CommentRepository;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotAuthorized;
import ewm.exception.NotFoundException;
import ewm.exception.ValidationException;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto create(PostCommentParam postCommentParam) {
        Comment comment = commentMapper.postToComment(postCommentParam);
        LocalDateTime eventDate = comment.getEvent().getEventDate();
        comment.setStatus(CommentStatus.PENDING);
        Comment savedComment = commentRepository.save(comment);
        log.info("Created new comment {}", savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto update(UpdateCommentParam updCommentParam) {
        Comment comment = commentRepository.findById(updCommentParam.commentId()).orElseThrow(
                () ->  new NotFoundException(String.format("Comment with id=%d was not found", updCommentParam.commentId())));

        userRepository.findById(updCommentParam.author()).orElseThrow(
                () ->  new NotFoundException(String.format("User with id=%d was not found", updCommentParam.author())));

        if (comment.getAuthor().getId() != updCommentParam.author()) {
            throw new NotAuthorized("Comment can be edited only by its author.");
        }

        comment.setComment(updCommentParam.comment());
        Comment savedComment = commentRepository.save(comment);
        log.info("Updated comment {}", savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () ->  new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (comment.getAuthor().getId() != userId) {
            throw new NotAuthorized("Comment can be deleted only by its author.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> findAllByAuthor(Long userId) {
        BooleanExpression byAuthorId = QComment.comment1.author.id.eq(userId);
        Iterable<Comment> comments = commentRepository.findAll(byAuthorId);
        List<CommentDto> commentsDto = StreamSupport.stream(comments.spliterator(), false)
                .map(commentMapper::toCommentDto)
                .toList();

        return commentsDto;
    }

    @Override
    public List<CommentDto> findAllByEventAndAuthor(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () ->  new NotFoundException(String.format("User with id=%d was not found", userId)));
        eventRepository.findById(eventId).orElseThrow(
                () ->  new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        BooleanExpression byEventAndAuthorId = QComment.comment1.author.id.eq(userId)
                .and(QComment.comment1.event.id.eq(eventId));
        Iterable<Comment> comments = commentRepository.findAll(byEventAndAuthorId);
        List<CommentDto> commentsDto = StreamSupport.stream(comments.spliterator(), false)
                .map(commentMapper::toCommentDto)
                .toList();

        return commentsDto;
    }

    @Override
    public CommentDto findByIdAndAuthor(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () ->  new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
        userRepository.findById(userId).orElseThrow(
                () ->  new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (comment.getAuthor().getId() != userId) {
            throw new NotAuthorized("Only author is allowed to see this comment");
        }

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> searchComments(AdminCommentSearchFilter filter) {
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
    public CommentDto findCommentById(Long commentId) {
        log.info("Admin find comment id={}", commentId);
        return commentMapper.toCommentDto(existsComment(commentId));
    }

    @Override
    @Transactional
    public CommentDto updateStatusComment(Long commentId, UpdateCommentStatusRequest request) {
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
        return commentMapper.toCommentDto(commentRepository.save(comment));
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
    @Override
    public List<CommentDto> getPublishedComments(String text, List<Long> events, String rangeStart, String rangeEnd, int from, int size, String sort) {
    Sort sortBy = Sort.by("created").descending();
    if (sort != null && sort.equalsIgnoreCase("asc")) {
        sortBy = Sort.by("created").ascending();
    }
    Pageable pageable = PageRequest.of(from / size, size, sortBy);

    LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, FORMATTER) : null;
    LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, FORMATTER) : null;

        List<Comment> comments = commentRepository.findPublishedCommentsWithFilters(text, events, start, end, pageable);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getPublishedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));
        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new NotFoundException("Comment not found or not published");
        }
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getPublishedCommentsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
        List<Comment> comments = commentRepository.findByEventAndStatus(event, CommentStatus.PUBLISHED);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}