package ewm.user.mapper;

import ewm.user.dto.UserDto;
import ewm.user.dto.UserPostDto;
import ewm.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);

    User userPostDtoToUser(UserPostDto userPostDto);
}
