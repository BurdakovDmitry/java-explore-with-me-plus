package ewm.comments.mapper;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.PostCommentDto;
import ewm.comments.model.Comment;
import ewm.event.mapper.EventMapper;
import ewm.event.service.PrivateEventService;
import ewm.user.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {PrivateEventService.class, EventMapper.class, UserMapper.class})
public interface CommentMapper {

    CommentDto toCommentDto(Comment comment);

    Comment postDtoToComment(PostCommentDto postCommentDto);

}
