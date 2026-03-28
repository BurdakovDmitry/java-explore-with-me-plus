package ewm.event.dto;

import ewm.common.dto.LocationDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;

    // Геттеры
    public String getAnnotation() { return annotation; }
    public Long getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public LocationDto getLocation() { return location; }
    public Boolean getPaid() { return paid; }
    public Integer getParticipantLimit() { return participantLimit; }
    public Boolean getRequestModeration() { return requestModeration; }
    public String getStateAction() { return stateAction; }
    public String getTitle() { return title; }

    // Сеттеры
    public void setAnnotation(String annotation) { this.annotation = annotation; }
    public void setCategory(Long category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public void setLocation(LocationDto location) { this.location = location; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setParticipantLimit(Integer participantLimit) { this.participantLimit = participantLimit; }
    public void setRequestModeration(Boolean requestModeration) { this.requestModeration = requestModeration; }
    public void setStateAction(String stateAction) { this.stateAction = stateAction; }
    public void setTitle(String title) { this.title = title; }
}