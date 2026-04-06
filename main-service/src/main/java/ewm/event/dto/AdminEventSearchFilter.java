package ewm.event.dto;

import ewm.event.model.EventState;
import java.time.LocalDateTime;
import java.util.List;

public record AdminEventSearchFilter(
        List<Long> users,
        List<EventState> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Integer from,
        Integer size
) {}