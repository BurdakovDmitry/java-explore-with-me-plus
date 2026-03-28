package ewm.pub.compilation.service;

import ewm.common.model.Compilation;
import ewm.common.model.Event;
import ewm.exception.NotFoundException;
import ewm.pub.compilation.dto.CompilationDto;
import ewm.pub.compilation.repository.CompilationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Getting compilations: pinned={}, from={}, size={}", pinned, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        return compilations.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info("Getting compilation by id: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));

        return toCompilationDto(compilation);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());

        if (compilation.getEvents() != null) {
            List<CompilationDto.EventShortDto> eventShortDtos = compilation.getEvents().stream()
                    .map(this::toEventShortDto)
                    .collect(Collectors.toList());
            dto.setEvents(eventShortDtos);
        }

        return dto;
    }

    private CompilationDto.EventShortDto toEventShortDto(Event event) {
        CompilationDto.EventShortDto dto = new CompilationDto.EventShortDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate().toString());
        dto.setPaid(event.getPaid());
        // TODO: добавить category, initiator, confirmedRequests, views
        return dto;
    }
}