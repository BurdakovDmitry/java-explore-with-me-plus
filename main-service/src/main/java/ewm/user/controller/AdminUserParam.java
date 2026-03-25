package ewm.user.controller;

import java.util.List;

public record AdminUserParam(
        List<Long> ids,
        Integer from,
        Integer size) {
}
