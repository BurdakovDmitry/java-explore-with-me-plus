package ewm.comments.repository;

import ewm.comments.model.Comment;
import ewm.comments.model.CommentStatus;
import ewm.event.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findPublishedCommentsWithFilters(String text, List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    List<Comment> findByEventAndStatus(Event event, CommentStatus status);
}