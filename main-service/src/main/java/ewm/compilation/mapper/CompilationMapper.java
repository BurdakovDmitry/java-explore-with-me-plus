package ewm.compilation.mapper;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.NewCompilationDto;
import ewm.compilation.dto.UpdateCompilationDto;
import ewm.compilation.model.Compilation;
import ewm.event.mapper.EventMapper;
import ewm.event.service.AdminEventService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;

@Mapper(componentModel = "spring",
        uses = {AdminEventService.class, EventMapper.class},
        imports = Collections.class)
public interface CompilationMapper {

    CompilationDto compilationToDto(Compilation compilation);

    @Mapping(target = "events", source = "newCompilationDto.events")
    @Mapping(target = "pinned", source = "newCompilationDto.pinned", defaultExpression  = "java(false)")
    Compilation postDtoToCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", source = "updCompilationDto.events", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "pinned", source = "updCompilationDto.pinned", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", source = "updCompilationDto.title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Compilation updateDtoToCompilation(UpdateCompilationDto updCompilationDto);
}
