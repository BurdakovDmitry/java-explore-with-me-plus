package ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.category.dto.model.Category;
import ewm.common.model.Location;
import ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(
        Long id,
        String annotation,
        Category category,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views,
        Location location
) {
}