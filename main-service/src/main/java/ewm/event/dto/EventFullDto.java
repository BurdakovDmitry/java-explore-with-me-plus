package ewm.event.dto;

import ewm.category.dto.model.Category;
import ewm.common.model.Location;
import ewm.user.dto.UserShortDto;

public record EventFullDto(
        Long id,
        String annotation,
        Category category,
        Long confirmedRequests,
        String createdOn,
        String description,
        String eventDate,
        UserShortDto initiator,
        Location location,
        Boolean paid,
        Integer participantLimit,
        String publishedOn,
        Boolean requestModeration,
        String state,
        String title,
        Long views
) {}