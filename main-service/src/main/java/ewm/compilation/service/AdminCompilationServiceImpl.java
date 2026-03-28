package ewm.compilation.service;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.CompilationPostDto;
import ewm.compilation.mapper.CompilationMapper;
import ewm.compilation.model.Compilation;
import ewm.compilation.repository.CompilationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(CompilationPostDto compilationPostDto) {
        Compilation compilation = compilationMapper.postDtoToCompilation(compilationPostDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Create new compilation {}", savedCompilation);
        return compilationMapper.compilationToDto(savedCompilation);
    }
}
