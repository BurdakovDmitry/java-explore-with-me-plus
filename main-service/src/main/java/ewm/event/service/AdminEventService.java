package ewm.event.service;

import ewm.event.model.Event;

import java.util.List;

public interface AdminEventService {
    List<Event> findByIds(List<Long> eventIds);
}
