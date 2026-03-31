package ewm.compilation.service;

import ewm.compilation.dto.CompilationDto;
import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

}