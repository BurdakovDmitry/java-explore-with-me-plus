package ewm.common.dto;

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

    // Геттеры
    public Long getId() { return id; }
    public String getAnnotation() { return annotation; }
    public CategoryDto getCategory() { return category; }
    public Long getConfirmedRequests() { return confirmedRequests; }
    public String getEventDate() { return eventDate; }
    public UserShortDto getInitiator() { return initiator; }
    public Boolean getPaid() { return paid; }
    public String getTitle() { return title; }
    public Long getViews() { return views; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
    public void setCategory(CategoryDto category) { this.category = category; }
    public void setConfirmedRequests(Long confirmedRequests) { this.confirmedRequests = confirmedRequests; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setInitiator(UserShortDto initiator) { this.initiator = initiator; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setTitle(String title) { this.title = title; }
    public void setViews(Long views) { this.views = views; }
}