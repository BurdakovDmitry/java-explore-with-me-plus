package ewm.event.service;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto);
}
