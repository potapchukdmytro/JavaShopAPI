package shop.dto.category;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCategoryDTO {
    private String name;
    private MultipartFile file;
    private String description;
}
