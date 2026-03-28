package ewm.event.service;

import ewm.common.dto.EventFullDto;
import ewm.common.dto.EventShortDto;
import ewm.common.dto.NewEventDto;
import ewm.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);
}