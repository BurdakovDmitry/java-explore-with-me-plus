package ewm.event.service;

import ewm.event.dto.*;
import ewm.event.model.Event;
import ewm.user.dto.UserShortDto;
import ewm.user.model.User;
import ewm.event.repository.EventRepository;
import ewm.exception.NotFoundException;
import ewm.exception.ValidationException;
import ewm.user.repository.UserRepository;
import ewm.common.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.info("Getting events for user id={}, from={}, size={}", userId, from, size);

        getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        return events.stream()
                .map(this::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event for user id={}: {}", userId, newEventDto);

        User user = getUserOrThrow(userId);

        if (newEventDto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours from now");
        }

        Event event = new Event();
        event.setAnnotation(newEventDto.annotation());
        event.setDescription(newEventDto.description());
        event.setEventDate(newEventDto.eventDate());
        event.setPaid(newEventDto.paid());
        event.setParticipantLimit(newEventDto.participantLimit());
        event.setRequestModeration(newEventDto.requestModeration());
        event.setTitle(newEventDto.title());
        event.setInitiator(user);
        event.setState("PENDING");
        event.setCreatedOn(LocalDateTime.now());

        if (newEventDto.location() != null) {
            event.setLocation(new Location(newEventDto.location().lat(), newEventDto.location().lon()));
        }

        Event savedEvent = eventRepository.save(event);
        return toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.info("Getting event id={} for user id={}", eventId, userId);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Event does not belong to user");
        }

        return toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Updating event id={} for user id={}: {}", eventId, userId, updateRequest);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Event does not belong to user");
        }

        if (!event.getState().equals("PENDING") && !event.getState().equals("CANCELED")) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }

        if (updateRequest.annotation() != null) event.setAnnotation(updateRequest.annotation());
        if (updateRequest.description() != null) event.setDescription(updateRequest.description());
        if (updateRequest.eventDate() != null) {
            if (updateRequest.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Event date must be at least 2 hours from now");
            }
            event.setEventDate(updateRequest.eventDate());
        }
        if (updateRequest.paid() != null) event.setPaid(updateRequest.paid());
        if (updateRequest.participantLimit() != null) event.setParticipantLimit(updateRequest.participantLimit());
        if (updateRequest.requestModeration() != null) event.setRequestModeration(updateRequest.requestModeration());
        if (updateRequest.title() != null) event.setTitle(updateRequest.title());

        if (updateRequest.location() != null) {
            event.setLocation(new Location(updateRequest.location().lat(), updateRequest.location().lon()));
        }

        if (updateRequest.stateAction() != null) {
            switch (updateRequest.stateAction()) {
                case "SEND_TO_REVIEW" -> event.setState("PENDING");
                case "CANCEL_REVIEW" -> event.setState("CANCELED");
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return toEventFullDto(updatedEvent);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getEventDate().toString(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                0L, // views пока нет
                event.getLocation()
        );
    }

    private EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                0L, // confirmedRequests пока нет
                event.getCreatedOn().toString(),
                event.getDescription(),
                event.getEventDate().toString(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() != null ? event.getPublishedOn().toString() : null,
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                0L // views пока нет
        );
    }
}