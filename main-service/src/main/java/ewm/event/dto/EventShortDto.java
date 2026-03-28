package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.user.dto.UserShortDto;
import lombok.Data;

@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}