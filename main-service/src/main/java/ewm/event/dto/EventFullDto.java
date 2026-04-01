package ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.category.model.Category;
import ewm.event.model.Location;
import ewm.user.dto.UserShortDto;
import ewm.event.model.EventState;

import java.time.LocalDateTime;

public record EventFullDto(
        Long id,
        String annotation,
        Category category,
        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        UserShortDto initiator,
        Location location,
        Boolean paid,
        Integer participantLimit,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,

        Boolean requestModeration,
        EventState state,
        String title,
        Long views
) {}
