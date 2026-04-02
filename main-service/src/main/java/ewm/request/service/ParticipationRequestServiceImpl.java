package ewm.request.service;

import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.repository.EventRepository;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.exception.ValidationException;
import ewm.request.dto.ParticipationRequestDto;
import ewm.request.mapper.ParticipationRequestMapper;
import ewm.request.model.ParticipationRequest;
import ewm.request.model.ParticipationStatus;
import ewm.request.repository.ParticipationRequestRepository;
import ewm.user.model.User;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        User requester = findUserById(userId);

        List<ParticipationRequest> requests = requestRepository.findByRequester(requester);

        log.info("Получен список заявок на участия в событиях пользователя с id = {}", userId);
        return requests.stream()
                .map(requestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = findUserById(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));

        ParticipationRequest request = new ParticipationRequest();
        Long confirmedRequests = requestRepository.countByEventAndStatus(event, ParticipationStatus.CONFIRMED);

        if (requestRepository.existsByRequesterAndEvent(requester, event)) {
            throw new ConflictException("Данный запрос уже существует");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие еще не опубликовано");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Достигнут лимит участников на данное событие");
        }

        if (event.getRequestModeration() == false) {
            request.setStatus(ParticipationStatus.CONFIRMED);
        } else {
            request.setStatus(ParticipationStatus.PENDING);
        }

        request.setRequester(requester);
        request.setEvent(event);

        ParticipationRequest saveRequest = requestRepository.save(request);

        log.info("Запрос на участие в событии добавлен");

        return requestMapper.mapToRequestDto(saveRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        findUserById(userId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с id = " + requestId + " не найдена"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("Можно отменить только свою заявку");
        }

        request.setStatus(ParticipationStatus.CANCELED);
        requestRepository.save(request);

        log.info("Заявка на событие отменена");

        return requestMapper.mapToRequestDto(request);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
