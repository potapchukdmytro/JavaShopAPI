package shop.controllers;

import org.springframework.web.bind.annotation.*;
import shop.dto.CategoryDTO;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HomeController {
    private static List<CategoryDTO> list = new ArrayList<>();

    @GetMapping("/")
    public List<CategoryDTO> index() {
        return list;
    }

    @PostMapping("/")
    public void add(@RequestBody CategoryDTO category) {
        list.add(category);
    }

    @PostMapping("/delete")
    public void delete(int id) {
        for (CategoryDTO category: list) {
            if(category.getId() == id) {
                list.remove(category);
                return;
            }
        }
    }

    @PostMapping("/update")
    public void update(CategoryDTO newCategory) {
        for (CategoryDTO category: list) {
            if(category.getId() == newCategory.getId()) {
                category.setName(newCategory.getName());
                return;
            }
        }
    }
}
