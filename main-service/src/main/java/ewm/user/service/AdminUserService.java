package ewm.user.service;

import ewm.user.dto.AdminUserParam;
import ewm.user.dto.UserDto;
import ewm.user.dto.UserPostDto;

import java.util.List;

public interface AdminUserService {
    UserDto create(UserPostDto userPostDto);

    List<UserDto> findAll(AdminUserParam params);

    void delete(Long userId);
}
