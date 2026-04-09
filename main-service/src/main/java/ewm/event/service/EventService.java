package ewm.event.service;

import ewm.event.dto.AdminEventSearchFilter;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.dto.PublicEventParamDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.dto.UpdateEventUserRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId, HttpServletRequest request);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<EventShortDto> getEventsPublic(PublicEventParamDto paramDto, HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);

    List<EventFullDto> searchEventsAdmin(AdminEventSearchFilter filter);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto);
}