package ewm.comments.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.comments.dto.CommentDto;
import ewm.comments.dto.CommentSearchParams;
import ewm.comments.dto.PostCommentParam;
import ewm.comments.dto.UpdateCommentParam;
import ewm.comments.mapper.CommentMapper;
import ewm.comments.model.Comment;
import ewm.comments.model.CommentStatus;
import ewm.comments.model.QComment;
import ewm.comments.repository.CommentRepository;
import ewm.event.repository.EventRepository;
import ewm.exception.NotAuthorized;
import ewm.exception.NotFoundException;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                () -> new NotFoundException(String.format("Comment with id=%d was not found", updCommentParam.commentId())));

        userRepository.findById(updCommentParam.author()).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", updCommentParam.author())));

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
                () -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

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
                () -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

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
                () -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (comment.getAuthor().getId() != userId) {
            throw new NotAuthorized("Only author is allowed to see this comment");
        }

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getPublishedComments(CommentSearchParams params) {
        // Проверка дат
        if (params.rangeStart() != null && params.rangeEnd() != null) {
            if (params.rangeStart().isAfter(params.rangeEnd())) {
                throw new IllegalArgumentException("rangeStart не может быть позже rangeEnd");
            }
        }

        Sort sortBy = Sort.by("createdOn").descending();
        if (params.sort() != null && params.sort().equalsIgnoreCase("asc")) {
            sortBy = Sort.by("createdOn").ascending();
        }
        Pageable pageable = PageRequest.of(params.from() / params.size(), params.size(), sortBy);

        // QueryDSL
        QComment qComment = QComment.comment1;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(qComment.status.eq(CommentStatus.PUBLISHED));

        if (params.text() != null && !params.text().isBlank()) {
            predicate.and(qComment.comment.containsIgnoreCase(params.text()));
        }
        if (params.events() != null && !params.events().isEmpty()) {
            predicate.and(qComment.event.id.in(params.events()));
        }
        if (params.rangeStart() != null) {
            predicate.and(qComment.createdOn.goe(params.rangeStart()));
        }
        if (params.rangeEnd() != null) {
            predicate.and(qComment.createdOn.loe(params.rangeEnd()));
        }

        List<Comment> comments = commentRepository.findAll(predicate, pageable).getContent();

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
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
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        QComment qComment = QComment.comment1;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(qComment.event.id.eq(eventId));
        predicate.and(qComment.status.eq(CommentStatus.PUBLISHED));

        List<Comment> comments = new ArrayList<>();
        commentRepository.findAll(predicate).forEach(comments::add);

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }
}