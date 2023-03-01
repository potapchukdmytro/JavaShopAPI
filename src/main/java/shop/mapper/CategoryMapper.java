package shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import shop.dto.category.CategoryItemDTO;
import shop.dto.category.CreateCategoryDTO;
import shop.entities.CategoryEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryItemDTO categoryItemDTOByCategory(CategoryEntity category);
    List<CategoryItemDTO> CategoryItemDTOsByCategories(List<CategoryEntity> categories);

    @Mapping(target = "image", ignore = true)
    CategoryEntity CategoryByCategoryCreateDTO(CreateCategoryDTO createCategoryDTO);
}
