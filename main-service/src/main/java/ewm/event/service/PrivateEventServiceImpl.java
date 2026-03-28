package ewm.event.service;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.model.Event;
import ewm.user.model.User;
import ewm.event.dto.UpdateEventUserRequest;
import ewm.event.repository.EventRepository;
import ewm.exception.NotFoundException;
import ewm.exception.ValidationException;
import ewm.user.repository.UserRepository;
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

        // Проверяем существование пользователя
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


        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours from now");
        }

        // Создаём событие
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);
        event.setParticipantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0);
        event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
        event.setTitle(newEventDto.getTitle());
        event.setInitiator(user);
        event.setState("PENDING");
        event.setCreatedOn(LocalDateTime.now());

        // Временно устанавливаю категорию по ID (потом заменим на Category из БД)
        // TODO: после добавления CategoryRepository
        // Category category = categoryRepository.findById(newEventDto.getCategory())
        //        .orElseThrow(() -> new NotFoundException("Category not found"));
        // event.setCategory(category);

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

        // Проверяем, что событие принадлежит пользователю
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Event does not belong to user");
        }

        if (!event.getState().equals("PENDING") && !event.getState().equals("CANCELED")) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Event date must be at least 2 hours from now");
            }
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
            } else if (updateRequest.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState("CANCELED");
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
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate().toString());
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        // TODO: добавить category, initiator, confirmedRequests, views
        return dto;
    }

    private EventFullDto toEventFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate().toString());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn().toString());
        // TODO: добавить category, initiator, location, confirmedRequests, views, publishedOn
        return dto;
    }
}