package ewm.event.mapper;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.model.Event;
import ewm.request.repository.ParticipationRequestRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "views", constant = "0L")
    EventShortDto toShortDto(Event event);

    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "views", constant = "0L")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "confirmedRequests",
            expression = "java(requestRepository != null ? requestRepository.countByEventAndStatus(event, ParticipationStatus.CONFIRMED) : 0L)")
    EventFullDto toFullDto(Event event,
                           @Context ParticipationRequestRepository requestRepository);
}