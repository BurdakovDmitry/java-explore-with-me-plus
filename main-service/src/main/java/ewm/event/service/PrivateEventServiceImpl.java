package ewm.event.service;

import client.StatClient;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import ewm.HitDto;
import ewm.ParamDto;
import ewm.StatsDto;
import ewm.event.dto.*;
import ewm.event.mapper.EventMapper;
import ewm.request.model.ConfirmedRequestCount;
import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.model.Location;
import ewm.event.model.QEvent;
import ewm.exception.ValidationException;
import ewm.request.model.ParticipationStatus;
import ewm.request.model.QParticipationRequest;
import ewm.request.repository.ParticipationRequestRepository;
import ewm.user.model.User;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final StatClient statClient = new StatClient("http://ewm-stats-server:9090");

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

        // Используем маппер для создания события
        Event event = eventMapper.toEvent(dto);
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

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

        // Используем маппер для обновления
        eventMapper.updateEventMap(dto, event);

        // Обрабатываем stateAction отдельно
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

    @Override
    public List<EventShortDto> getPublicEvents(PublicEventParamDto eventParamDto, HttpServletRequest request) {
        if (eventParamDto.rangeStart() != null && eventParamDto.rangeEnd() != null &&
                eventParamDto.rangeStart().isAfter(eventParamDto.rangeEnd())) {
            throw new ValidationException("End date cannot be before start date");
        }

        QEvent event = QEvent.event;
        QParticipationRequest parRequest = QParticipationRequest.participationRequest;
        BooleanBuilder paramFilter = new BooleanBuilder();

        if (eventParamDto.text() != null && !eventParamDto.text().isBlank()) {
            paramFilter.and(event.annotation.containsIgnoreCase(eventParamDto.text())
                    .or(event.description.containsIgnoreCase(eventParamDto.text())));
        }

        if (eventParamDto.category() != null && !eventParamDto.category().isEmpty()) {
            paramFilter.and(event.category.id.in(eventParamDto.category()));
        }

        if (eventParamDto.paid() != null) {
            paramFilter.and(event.paid.eq(eventParamDto.paid()));
        }

        LocalDateTime start = eventParamDto.rangeStart() != null ? eventParamDto.rangeStart() : LocalDateTime.now();
        paramFilter.and(event.eventDate.goe(start));

        if (eventParamDto.rangeEnd() != null) {
            paramFilter.and(event.eventDate.loe(eventParamDto.rangeEnd()));
        }

        paramFilter.and(event.state.eq(EventState.PUBLISHED));

        if (eventParamDto.onlyAvailable()) {
            paramFilter.and(event.participantLimit.eq(0)
                    .or(event.participantLimit.gt(JPAExpressions
                            .select(parRequest.count())
                            .from(parRequest)
                            .where(parRequest.event.id.eq(event.id)
                                    .and(parRequest.status.eq(ParticipationStatus.CONFIRMED)))
                    )));
        }

        Sort sortEventDate = Sort.unsorted();
        if (eventParamDto.sort() != null && eventParamDto.sort().equalsIgnoreCase("EVENT_DATE")) {
            sortEventDate = Sort.by("eventDate").ascending();
        }

        Pageable pageable = PageRequest.of(eventParamDto.from() / eventParamDto.size(),
                eventParamDto.size(), sortEventDate);

        List<Event> events = eventRepository.findAll(paramFilter, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long,Long> confirmedRequestsMap = requestRepository.findAllConfirmedRequests(eventIds).stream()
                .collect(Collectors.toMap(ConfirmedRequestCount::eventId, ConfirmedRequestCount::count));
        Map<Long, Long> viewsMap = getViewsMap(events);

        List<EventShortDto> shortsDto = events.stream()
                .map(eventMapper::toShortDto)
                .peek(shortDto -> {
                    shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
                    shortDto.setViews(viewsMap.getOrDefault(shortDto.getId(), 0L));
                })
                .toList();

        if (eventParamDto.sort() != null && eventParamDto.sort().equalsIgnoreCase("VIEWS")) {
            shortsDto.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        saveHit(request);

        log.info("Получен список запросов по указанным фильтрам");

        return shortsDto;
    }

    @Override
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = getEventOrThrow(id);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        ParamDto paramDto = new ParamDto(event.getPublishedOn(),
                LocalDateTime.now(),
                List.of(request.getRequestURI()),
                true);

        EventFullDto fullDto = eventMapper.toFullDto(event);

        fullDto.setConfirmedRequests(requestRepository.countByEventAndStatus(event, ParticipationStatus.CONFIRMED));
        fullDto.setViews(getViews(paramDto));

        saveHit(request);

        log.info("Получено событие с id = {}", id);

        return fullDto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private void saveHit(HttpServletRequest request) {
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());

        statClient.hit(hitDto);
    }

    private Long getViews(ParamDto paramDto) {
        List<StatsDto> views = statClient.get(paramDto);

        return views.isEmpty() ? 0L : views.getFirst().hits();
    }

    private Map<Long, Long> getViewsMap(List<Event> events) {
        String url = "/events/";
        List<String> uris = events.stream().map(event -> url + event.getId()).toList();

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        List<StatsDto> stats = statClient.get(new ParamDto(start, LocalDateTime.now(), uris, true));

        return stats.stream().collect(Collectors.toMap(statsDto ->
                        Long.parseLong(statsDto.uri().substring(statsDto.uri().lastIndexOf("/") + 1)),
                StatsDto::hits));
    }
}