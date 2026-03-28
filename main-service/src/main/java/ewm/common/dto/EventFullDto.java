package ewm.common.dto;

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

    // Геттеры
    public Long getId() { return id; }
    public String getAnnotation() { return annotation; }
    public CategoryDto getCategory() { return category; }
    public Long getConfirmedRequests() { return confirmedRequests; }
    public String getCreatedOn() { return createdOn; }
    public String getDescription() { return description; }
    public String getEventDate() { return eventDate; }
    public UserShortDto getInitiator() { return initiator; }
    public LocationDto getLocation() { return location; }
    public Boolean getPaid() { return paid; }
    public Integer getParticipantLimit() { return participantLimit; }
    public String getPublishedOn() { return publishedOn; }
    public Boolean getRequestModeration() { return requestModeration; }
    public String getState() { return state; }
    public String getTitle() { return title; }
    public Long getViews() { return views; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
    public void setCategory(CategoryDto category) { this.category = category; }
    public void setConfirmedRequests(Long confirmedRequests) { this.confirmedRequests = confirmedRequests; }
    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }
    public void setDescription(String description) { this.description = description; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setInitiator(UserShortDto initiator) { this.initiator = initiator; }
    public void setLocation(LocationDto location) { this.location = location; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setParticipantLimit(Integer participantLimit) { this.participantLimit = participantLimit; }
    public void setPublishedOn(String publishedOn) { this.publishedOn = publishedOn; }
    public void setRequestModeration(Boolean requestModeration) { this.requestModeration = requestModeration; }
    public void setState(String state) { this.state = state; }
    public void setTitle(String title) { this.title = title; }
    public void setViews(Long views) { this.views = views; }
}