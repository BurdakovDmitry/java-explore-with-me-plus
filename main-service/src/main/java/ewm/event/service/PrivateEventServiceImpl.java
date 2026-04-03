package ewm.event.service;

import ewm.event.dto.*;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.model.Location;
import ewm.user.model.User;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        log.info("Getting events for user id={}, from={}, size={}", userId, from, size);

        getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, pageable)
                .stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        log.info("Adding event for user id={}", userId);

        User user = getUserOrThrow(userId);

        if (dto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Event event = new Event();
        event.setAnnotation(dto.annotation());
        event.setDescription(dto.description());
        event.setEventDate(dto.eventDate());
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(dto.paid() != null ? dto.paid() : false);
        event.setParticipantLimit(dto.participantLimit() != null ? dto.participantLimit() : 0);
        event.setRequestModeration(dto.requestModeration() != null ? dto.requestModeration() : true);
        event.setTitle(dto.title());
        event.setInitiator(user);
        event.setState(EventState.PENDING);

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().getLat(), dto.location().getLon()));
        }

        Event saved = eventRepository.save(event);
        log.info("Event created successfully: id={}", saved.getId());

        return eventMapper.toFullDto(saved);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.info("Getting event id={} for user id={}", eventId, userId);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event does not belong to user");
        }

        return eventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("Updating event id={} for user id={}", eventId, userId);

        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event does not belong to user");
        }

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.eventDate() != null &&
                dto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        if (dto.annotation() != null) event.setAnnotation(dto.annotation());
        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.eventDate() != null) event.setEventDate(dto.eventDate());
        if (dto.paid() != null) event.setPaid(dto.paid());
        if (dto.participantLimit() != null) event.setParticipantLimit(dto.participantLimit());
        if (dto.requestModeration() != null) event.setRequestModeration(dto.requestModeration());
        if (dto.title() != null) event.setTitle(dto.title());

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().getLat(), dto.location().getLon()));
        }

        if (dto.stateAction() != null) {
            switch (dto.stateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        Event updated = eventRepository.save(event);
        log.info("Event updated successfully: id={}", updated.getId());

        return eventMapper.toFullDto(updated);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
    }
}