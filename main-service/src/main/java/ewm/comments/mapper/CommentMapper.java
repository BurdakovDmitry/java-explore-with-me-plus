package ewm.comments.mapper;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.PostCommentParam;
import ewm.comments.model.Comment;
import ewm.event.mapper.EventMapper;
import ewm.event.service.PrivateEventService;
import ewm.user.mapper.UserMapper;
import ewm.user.service.AdminUserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PrivateEventService.class, AdminUserService.class, EventMapper.class, UserMapper.class})
public interface CommentMapper {

    CommentDto toCommentDto(Comment comment);

    Comment postToComment(PostCommentParam postCommentParam);

    List<CommentDto> toFullDtoList(List<Comment> comments);
}
