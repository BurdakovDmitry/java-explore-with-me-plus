package ewm.event.service;

import ewm.category.model.Category;
import ewm.category.repository.CategoryRepository;
import ewm.common.model.Location;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.request.repository.ParticipationRequestRepository;
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
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<EventState> states,
                                           List<Long> categories, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Search events with filters: users={}, states={}, categories={}", users, states, categories);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findEventsByFilters(users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(event -> eventMapper.toFullDto(event, participationRequestRepository))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        log.info("Update event with ID: {}", eventId);

        Event event = existsEvent(eventId);

        if (dto.eventDate() != null && dto.eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Дата события должна быть не раньше, чем через час");
        }

        if (dto.annotation() != null) {
            event.setAnnotation(dto.annotation());
        }

        if (dto.description() != null) {
            event.setDescription(dto.description());
        }

        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }

        if (dto.paid() != null) {
            event.setPaid(dto.paid());
        }

        if (dto.participantLimit() != null) {
            event.setParticipantLimit(dto.participantLimit());
        }

        if (dto.requestModeration() != null) {
            event.setRequestModeration(dto.requestModeration());
        }

        if (dto.title() != null) {
            event.setTitle(dto.title());
        }

        if (dto.location() != null) {
            event.setLocation(new Location(dto.location().getLat(), dto.location().getLon()));
        }

        if (dto.category() != null) {
            Category category = categoryRepository.findById(dto.category())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.category() + " was not found"));
            event.setCategory(category);
        }

        if (dto.stateAction() != null) {
            switch (dto.stateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "Нельзя опубликовать события, пока оно находится не в нужном статусе (PENDING): "
                                        + event.getState());
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    log.info("Event с id={} успешно опубликовано", eventId);
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Невозможно отклонить опубликованное событие");
                    }
                    event.setState(EventState.CANCELED);
                    log.info("Event с id={} отклонено", eventId);
                }
            }
        }

        Event updated = eventRepository.save(event);
        log.info("Event c id={} успешно обновлено", updated.getId());

        return eventMapper.toFullDto(updated, participationRequestRepository);
    }

    private Event existsEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id = " + eventId + " was not found"));
    }
}