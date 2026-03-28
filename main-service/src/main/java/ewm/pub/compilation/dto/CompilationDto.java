package ewm.pub.compilation.dto;

import java.util.List;

public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;

    // Геттеры
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Boolean getPinned() { return pinned; }
    public List<EventShortDto> getEvents() { return events; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }
    public void setEvents(List<EventShortDto> events) { this.events = events; }

    public static class EventShortDto {
        private Long id;
        private String title;
        private String annotation;
        private String eventDate;
        private Boolean paid;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAnnotation() { return annotation; }
        public void setAnnotation(String annotation) { this.annotation = annotation; }
        public String getEventDate() { return eventDate; }
        public void setEventDate(String eventDate) { this.eventDate = eventDate; }
        public Boolean getPaid() { return paid; }
        public void setPaid(Boolean paid) { this.paid = paid; }
    }
}