package ewm.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    @NotNull(message = "Название категории не может быть пустым")
    @Size(min = 0, max = 50, message = "Имя категории не должно превышать 50 символом")
    private String name;
}
