package ewm.comments.service;

import ewm.comments.dto.CommentDto;
import ewm.comments.mapper.CommentMapper;
import ewm.comments.model.Comment;
import ewm.comments.model.CommentStatus;
import ewm.comments.repository.CommentRepository;
import ewm.event.model.Event;
import ewm.event.repository.EventRepository;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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