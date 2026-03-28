package ewm.pub.compilation.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;

    @Data
    public static class EventShortDto {
        private Long id;
        private String title;
        private String annotation;
        private String eventDate;
        private Boolean paid;
    }
}