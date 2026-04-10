package ewm.event.service;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.dto.PublicEventParamDto;
import ewm.event.dto.UpdateEventUserRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PrivateEventService {

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<EventShortDto> getPublicEvents(PublicEventParamDto paramDto, HttpServletRequest request);

    EventFullDto getPublicEventById(Long id, HttpServletRequest request);
}