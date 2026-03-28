package ewm.compilation.service;

import ewm.compilation.model.Compilation;
import ewm.compilation.repository.CompilationRepository;
import ewm.exception.NotFoundException;
import ewm.compilation.dto.CompilationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements ewm.compilation.service.PublicCompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        return compilations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return toDto(compilation);
    }

    private CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                null,                           // events
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}