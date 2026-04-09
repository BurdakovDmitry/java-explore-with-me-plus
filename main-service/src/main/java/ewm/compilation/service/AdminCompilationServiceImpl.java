package ewm.compilation.service;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.NewCompilationDto;
import ewm.compilation.dto.UpdateCompilationDto;
import ewm.compilation.mapper.CompilationMapper;
import ewm.compilation.model.Compilation;
import ewm.compilation.repository.CompilationRepository;
import ewm.event.model.Event;
import ewm.event.repository.EventRepository;
import ewm.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.postDtoToCompilation(newCompilationDto);

        if (newCompilationDto.events() != null) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.events());
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Create new compilation {}", savedCompilation);
        return compilationMapper.compilationToDto(savedCompilation);
    }

    @Override
    public void delete(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () ->  new NotFoundException(String.format("Compilation with id=%d was not found", compilationId)));
        log.info("Delete compilation with id {}", compilationId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto update(UpdateCompilationDto updCompilationDto, Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () ->  new NotFoundException(String.format("Compilation with id=%d was not found", compilationId)));

        if (updCompilationDto.events() != null) {
            List<Event> events = eventRepository.findAllById(updCompilationDto.events());
            compilation.setEvents(events);
        }

        compilationMapper.updateDtoToCompilation(compilation, updCompilationDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Recreate updated compilation {}", savedCompilation);
        return compilationMapper.compilationToDto(savedCompilation);
    }
}
