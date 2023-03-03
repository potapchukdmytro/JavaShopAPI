package shop.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import shop.dto.category.CategoryItemDTO;
import shop.dto.category.CreateCategoryDTO;
import shop.dto.category.UpdateCategoryDTO;
import shop.entities.CategoryEntity;
import shop.interfaces.CategoryService;
import shop.mapper.CategoryMapper;
import shop.repositories.CategoryRepository;
import shop.storage.StorageService;

import java.io.File;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;

    @Override
    public CategoryItemDTO create(CreateCategoryDTO model) {
        var fileName = storageService.save(model.getBase64());
        CategoryEntity category = categoryMapper.CategoryByCategoryCreateDTO(model);
        category.setImage(fileName);
        categoryRepository.save(category);
        return categoryMapper.categoryItemDTOByCategory(category);
    }

    @Override
    public List<CategoryItemDTO> get() {
        var list = categoryRepository.findAll();
        return categoryMapper.CategoryItemDTOsByCategories(list);
    }

    @Override
    public CategoryItemDTO update(int id, UpdateCategoryDTO model) {
        var catOptional = categoryRepository.findById(id);
        if (catOptional.isPresent()) {
            var category = catOptional.get();
            storageService.removeFile(category.getImage());
            var fileName = storageService.save(model.getBase64());
            category.setImage(fileName);
            category.setName(model.getName());
            category.setDescription(model.getDescription());
            categoryRepository.save(category);
            return categoryMapper.categoryItemDTOByCategory(category);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        CategoryEntity category = categoryRepository.findById(id).get();
        storageService.removeFile(category.getImage());
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryItemDTO get(int id) {
        var catOptional = categoryRepository.findById(id);
        return catOptional.map(categoryMapper::categoryItemDTOByCategory).orElse(null);
    }
}
