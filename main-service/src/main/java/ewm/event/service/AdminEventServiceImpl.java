package ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.event.model.Event;
import ewm.event.model.QEvent;
import ewm.event.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;

    @Override
    public List<Event> findByIds(List<Long> eventIds) {
        if (eventIds == null) {
            return null;
        }
        BooleanExpression byEventIds = QEvent.event.id.in(eventIds);
        log.info("Return users with ids={}", eventIds);
        Iterable<Event> events = eventRepository.findAll(byEventIds);
        return StreamSupport.stream(events.spliterator(), false)
                .sorted(Comparator.comparing(Event::getId))
                .toList();
    }
}
