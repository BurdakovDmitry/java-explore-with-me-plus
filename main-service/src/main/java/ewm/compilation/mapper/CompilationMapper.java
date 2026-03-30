package ewm.compilation.mapper;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.CompilationPostDto;
import ewm.compilation.model.Compilation;
import ewm.event.service.AdminEventService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CompilationMapper {

    @Autowired
    protected AdminEventService adminEventService;

    public abstract CompilationDto compilationToDto(Compilation compilation);

    @Mapping(target = "events", expression = "java(adminEventService.findByIds(compilationPostDto.events()))")
    public abstract Compilation postDtoToCompilation(CompilationPostDto compilationPostDto);
}
