package ewm.event.service;

import ewm.event.dto.AdminEventSearchFilter;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.model.Event;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEvents(AdminEventSearchFilter filter);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto);

    public List<Event> findByIds(List<Long> eventIds);
}
