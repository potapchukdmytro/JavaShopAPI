package shop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dto.category.CreateCategoryDTO;
import shop.dto.category.UpdateCategoryDTO;
import shop.entities.CategoryEntity;
import shop.repositories.CategoryRepository;

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

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> index() {
        var list = categoryRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CategoryEntity> create(@ModelAttribute CreateCategoryDTO model) {
        CategoryEntity category = new CategoryEntity();
        category.setName(model.getName());
        category.setDescription(model.getDescription());

        try {
            String fileName = generateFilename(model.getImage().getOriginalFilename());
            //Path path = Paths.get("static/images/" + fileName);
            var url = this.getClass().getClassLoader();
            File newFile = new File(url + fileName);
            OutputStream os = new FileOutputStream(newFile);
            os.write(model.getImage().getBytes());
            os.close();
            //Files.write(path, model.getImage().getBytes());
            category.setImage(fileName);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        categoryRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
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
        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
