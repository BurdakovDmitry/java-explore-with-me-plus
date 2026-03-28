package ewm.event.dto;


import ewm.category.dto.CategoryDto;
import ewm.common.dto.LocationDto;
import ewm.user.dto.UserShortDto;
import lombok.Data;

@Data
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;
}