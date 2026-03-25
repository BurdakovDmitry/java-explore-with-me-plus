package ewm.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.exception.NotFoundException;
import ewm.user.controller.AdminUserParam;
import ewm.user.dto.UserDto;
import ewm.user.dto.UserPostDto;
import ewm.user.mapper.UserMapper;
import ewm.user.model.QUser;
import ewm.user.model.User;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserPostDto userPostDto) {
        User user = userMapper.userPostDtoToUser(userPostDto);
        user = userRepository.save(user);
        log.info("Create new user {}", user);
        return userMapper.userToUserDto(user);
    }

    public List<UserDto> findAll(AdminUserParam params) {
        Iterable<User> users;

        if (params.ids() == null) {
            log.info("Return all users");
            users = userRepository.findAll();
        } else {
            BooleanExpression byUserIds = QUser.user.id.in(params.ids());
            log.info("Return users with ids={}", params.ids());
            users = userRepository.findAll(byUserIds);
        }

        List<UserDto> usersDto = StreamSupport.stream(users.spliterator(), false)
                .sorted(Comparator.comparing(User::getId))
                .map(userMapper::userToUserDto)
                .toList();

        if (params.from() != null) {
            usersDto = usersDto.subList(params.from(), usersDto.size());
        }

        if (params.size() != null) {
            usersDto = usersDto.subList(0, params.size());
        }

        return usersDto;
    }

    public void delete(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        } else {
            log.info("Delete user with id {}", userId);
            userRepository.delete(user.get());
        }
    }
}
