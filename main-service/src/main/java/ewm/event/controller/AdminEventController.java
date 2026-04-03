package ewm.event.controller;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.model.EventState;
import ewm.event.service.AdminEventServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventServiceImpl adminEventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required=false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("GET /admin/events: users={}, states={}, categories={}, from={}, size={}",
                users, states, categories, from, size);
        return adminEventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        log.info("PATCH /admin/events/{}: {}", eventId, updateRequest);
        return adminEventService.updateEvent(eventId, updateRequest);
    }
}
