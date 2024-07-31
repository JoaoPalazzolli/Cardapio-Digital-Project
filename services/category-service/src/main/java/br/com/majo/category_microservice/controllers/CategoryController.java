package br.com.majo.category_microservice.controllers;

import br.com.majo.category_microservice.dtos.CategoryDTO;
import br.com.majo.category_microservice.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll(){
        return categoryService.findAll();
    }

    @GetMapping(value = "/restaurant/{restaurantId}")
    public ResponseEntity<List<CategoryDTO>> findAllByRestaurant(@PathVariable(value = "restaurantId") UUID restaurantId){
        return categoryService.findAllByRestaurant(restaurantId);
    }

    @GetMapping(value = "/{id}/restaurant/{restaurantId}")
    public ResponseEntity<CategoryDTO> findByIdAndRestaurantId(
            @PathVariable(value = "id") String id,
            @PathVariable(value = "restaurantId") UUID restaurantId){
        return categoryService.findByIdAndRestaurantId(id, restaurantId);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){
        return categoryService.createCategory(categoryDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable(value = "id") String id,
                                                      @RequestBody CategoryDTO categoryDTO){
        return categoryService.updateCategory(id, categoryDTO);
    }

    @DeleteMapping(value = "/{id}/restaurant/{restaurantId}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") String id,
                                            @PathVariable(value = "restaurantId") UUID restaurantId){
        return categoryService.deteleCategory(id, restaurantId);
    }
}
