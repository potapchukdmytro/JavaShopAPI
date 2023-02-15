package shop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dto.CategoryDTO;
import shop.dto.category.CreateCategoryDTO;
import shop.entities.CategoryEntity;
import shop.repositories.CategoryRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> index() {
        var list = categoryRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryEntity> create(@RequestBody CreateCategoryDTO model) {
        CategoryEntity category = new CategoryEntity();
        category.setName(model.getName());
        categoryRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<CategoryEntity> update(@RequestBody CategoryDTO model) {
        var optional = categoryRepository.findById(model.getId());
        if(optional.isPresent()) {
            var newCategory = optional.get();
            newCategory.setName(model.getName());
            categoryRepository.save(newCategory);
            return new ResponseEntity<>(newCategory, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("delete")
    public ResponseEntity<CategoryEntity> delete(@RequestBody int id) {
        var optional = categoryRepository.findById(id);
        if(optional.isPresent()) {
            var category = optional.get();
            categoryRepository.delete(category);
            return new ResponseEntity<>(category, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
