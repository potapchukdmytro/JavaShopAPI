package shop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dto.category.CategoryItemDTO;
import shop.dto.category.CreateCategoryDTO;
import shop.dto.category.UpdateCategoryDTO;
import shop.entities.CategoryEntity;
import shop.mapper.CategoryMapper;
import shop.repositories.CategoryRepository;
import shop.storage.StorageService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<List<CategoryItemDTO>> index() {
        var list = categoryRepository.findAll();
        var model = categoryMapper.CategoryItemDTOsByCategories(list);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryItemDTO> create(@RequestBody CreateCategoryDTO model) {
        var fileName = storageService.save(model.getBase64());
        CategoryEntity category = categoryMapper.CategoryByCategoryCreateDTO(model);
        category.setImage(fileName);
        categoryRepository.save(category);
        var result = categoryMapper.categoryItemDTOByCategory(category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private String generateFilename(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString();
        return uniqueName + extension;
    }


    @GetMapping("{id}")
    public ResponseEntity<CategoryEntity> get(@PathVariable("id") int categoryId) {
        var catOptional = categoryRepository.findById(categoryId);
        if (catOptional.isPresent()) {
            return new ResponseEntity<>(catOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryEntity> update(@PathVariable("id") int categoryId,
                                                 @RequestBody UpdateCategoryDTO model) {
        var catOptional = categoryRepository.findById(categoryId);
        if (catOptional.isPresent()) {
            var cat = catOptional.get();
            cat.setName(model.getName());
            categoryRepository.save(cat);
            return new ResponseEntity<>(cat, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId).get();
        storageService.removeFile(category.getImage());
        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>("Катагорія знищена.", HttpStatus.OK);
    }
}
