package ewm.comments.mapper;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.CommentFullDto;
import ewm.comments.dto.PostCommentDto;
import ewm.comments.model.Comment;
import ewm.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "event.id", source = "event.id")
    @Mapping(target = "event.title", source = "event.title")
    @Mapping(target = "event.eventDate", source = "event.eventDate")
    CommentDto toCommentDto(Comment comment);

    Comment postDtoToComment(PostCommentDto postCommentDto);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentFullDto toFullDto(Comment comment);

    List<CommentFullDto> toFullDtoList(List<Comment> comments);
}
