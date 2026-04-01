package ewm.request.repository;

import ewm.event.model.Event;
import ewm.request.model.ParticipationRequest;
import ewm.request.model.ParticipationStatus;
import ewm.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    // Список своих заявок на участия в событиях
    List<ParticipationRequest> findByRequester(User requester);

    // Проверяем наличие такого запроса
    boolean existsByRequesterAndEvent(User requester, Event event);

    // Количество заявок на событие
    Long countByEventAndStatus(Event event, ParticipationStatus status);
}
