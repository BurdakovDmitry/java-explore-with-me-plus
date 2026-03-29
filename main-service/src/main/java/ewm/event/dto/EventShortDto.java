package ewm.event.dto;

import ewm.category.dto.model.Category;
import ewm.common.model.Location;
import ewm.user.dto.UserShortDto;

public record EventShortDto(
        Long id,
        String annotation,
        Category category,
        String eventDate,
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views,
        Location location
) {}