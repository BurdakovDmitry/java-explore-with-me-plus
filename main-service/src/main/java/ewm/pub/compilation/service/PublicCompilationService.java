package ewm.pub.compilation.service;

import ewm.pub.compilation.dto.CompilationDto;
import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId);
}