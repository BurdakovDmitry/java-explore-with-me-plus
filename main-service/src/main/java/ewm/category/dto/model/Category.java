package ewm.category.dto.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}