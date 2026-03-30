package ewm.compilation.service;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.CompilationPostDto;

public interface AdminCompilationService {
    CompilationDto create(CompilationPostDto compilationPostDto);

}
