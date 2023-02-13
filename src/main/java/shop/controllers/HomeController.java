package shop.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
}
