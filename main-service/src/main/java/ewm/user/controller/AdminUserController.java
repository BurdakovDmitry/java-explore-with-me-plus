package ewm.user.controller;

import ewm.user.dto.UserDto;
import ewm.user.dto.UserPostDto;
import ewm.user.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    public final AdminUserService adminUserService;

    @GetMapping
    public List<UserDto> findAll(@RequestParam(required = false) List<Long> ids,
                                 @RequestParam(required = false) Integer from,
                                 @RequestParam(required = false) Integer size) {
        AdminUserParam params = new AdminUserParam(ids, from, size);
        return adminUserService.findAll(params);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated()
    public UserDto create(@Valid @RequestBody UserPostDto user) {
        return adminUserService.create(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminUserService.delete(userId);
    }

}
