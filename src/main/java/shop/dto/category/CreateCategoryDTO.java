package shop.dto.category;

import lombok.Data;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCategoryDTO {
    private String name;
    private MultipartFile image;
    private String description;
}
