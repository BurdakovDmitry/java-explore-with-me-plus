package ewm.compilation.mapper;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.NewCompilationDto;
import ewm.compilation.dto.UpdateCompilationDto;
import ewm.compilation.model.Compilation;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.service.PrivateEventService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PrivateEventService.class, EventMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    CompilationDto compilationToDto(Compilation compilation);

    @Mapping(target = "pinned", defaultExpression  = "java(false)")
    Compilation postDtoToCompilation(NewCompilationDto newCompilationDto);

    void updateDtoToCompilation(@MappingTarget Compilation compilation, UpdateCompilationDto updCompilationDto);

    default List<Event> mapEventIds(List<Long> eventIds, @Context PrivateEventService eventService) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }
        return eventService.findByIds(eventIds);
    }
}
