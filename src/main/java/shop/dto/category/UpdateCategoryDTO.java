package shop.dto.category;

import lombok.Data;

@Data
public class UpdateCategoryDTO {
    private String name;
    private String base64;
    private String description;
}
