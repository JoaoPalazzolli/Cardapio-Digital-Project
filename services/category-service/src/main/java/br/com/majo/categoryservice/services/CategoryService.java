package br.com.majo.categoryservice.services;

import br.com.majo.categoryservice.controllers.CategoryController;
import br.com.majo.categoryservice.domains.CategoryDomain;
import br.com.majo.categoryservice.dtos.CategoryDTO;
import br.com.majo.categoryservice.infra.exceptions.CategoryAlreadyExistException;
import br.com.majo.categoryservice.infra.exceptions.CategoryNotFoundException;
import br.com.majo.categoryservice.infra.utils.Mapper;
import br.com.majo.categoryservice.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<List<CategoryDTO>> findAll(){
        log.info("Finding all categories");

        var dtos = Mapper.parseListObject(categoryRepository.findAll(), CategoryDTO.class);

        dtos.forEach(x -> x.add(linkTo(methodOn(CategoryController.class).findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel()));

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<CategoryDTO> findByIdAndRestaurantId(String id, UUID restaurantId) {
        log.info("Finding category by id. (category id: ({}))", id);

        var dto = Mapper.parseObject(categoryRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found")), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findByIdAndRestaurantId(id, restaurantId)).withSelfRel());

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<CategoryDTO> createCategory(CategoryDTO categoryDTO) {
        if(categoryAlreadyExist(categoryDTO.getName(), categoryDTO.getRestaurantId())){
            throw new CategoryAlreadyExistException("This category already exist");
        }

        var category = Mapper.parseObject(categoryDTO, CategoryDomain.class);
        category.setCreateAt(new Date());

        var dto = Mapper.parseObject(categoryRepository.save(category), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findByIdAndRestaurantId(category.getId(), category.getRestaurantId())).withSelfRel());

        log.info("Success created category, (category id: ({}))", dto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    public ResponseEntity<CategoryDTO> updateCategory(String id, CategoryDTO categoryDTO) {
        var category = categoryRepository.findByIdAndRestaurantId(id, categoryDTO.getRestaurantId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if(categoryAlreadyExist(categoryDTO.getName(), category.getRestaurantId()) &&
                !categoryDTO.getName().equalsIgnoreCase(category.getName())){
            throw new CategoryAlreadyExistException("This category already exist");
        }

        categoryDTO.setRestaurantId(category.getRestaurantId());
        categoryDTO.setProducts(category.getProducts());
        categoryDTO.setCreateAt(category.getCreateAt());
        categoryDTO.setId(id);
        category = Mapper.parseObject(categoryDTO, CategoryDomain.class);

        var dto = Mapper.parseObject(categoryRepository.save(category), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findByIdAndRestaurantId(id, category.getRestaurantId())).withSelfRel());

        log.info("Success updated category. (category id: ({}))", id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> deteleCategory(String id, UUID restaurantId) {
        var category = categoryRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);

        log.info("Success deleted category. (category id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<CategoryDTO>> findAllByRestaurant(UUID restaurantId) {
        log.info("Finding all categories by restaurant");

        var dtos = Mapper.parseListObject(categoryRepository.findAllByRestaurantId(restaurantId), CategoryDTO.class);

        dtos.forEach(x -> x.add(linkTo(methodOn(CategoryController.class).findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel()));

        return ResponseEntity.ok(dtos);
    }

    private Boolean categoryAlreadyExist(String name, UUID restaurantId){
        return categoryRepository.findByNameAndRestaurantId(name, restaurantId).isPresent();
    }
}
