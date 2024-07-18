package br.com.majo.category_microservice.services;

import br.com.majo.category_microservice.controllers.CategoryController;
import br.com.majo.category_microservice.domains.CategoryDomain;
import br.com.majo.category_microservice.dtos.CategoryDTO;
import br.com.majo.category_microservice.infra.exceptions.CategoryAlreadyExistException;
import br.com.majo.category_microservice.infra.exceptions.CategoryNotFoundException;
import br.com.majo.category_microservice.infra.utils.Mapper;
import br.com.majo.category_microservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CategoryService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<List<CategoryDTO>> findAll(){
        logger.info("Finding all categories");

        var dtos = Mapper.parseListObject(categoryRepository.findAll(), CategoryDTO.class);

        dtos.forEach(x -> x.add(linkTo(methodOn(CategoryController.class).findById(x.getId())).withSelfRel()));

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<CategoryDTO> findById(String id) {
        logger.info("Finding category by id");

        var dto = Mapper.parseObject(categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found")), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findById(id)).withSelfRel());

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<CategoryDTO> createCategory(CategoryDTO categoryDTO) {

        if(categoryAlreadyExist(categoryDTO.getName())){
            throw new CategoryAlreadyExistException("This category already exist");
        }

        var category = Mapper.parseObject(categoryDTO, CategoryDomain.class);
        category.setCreateAt(new Date());

        var dto = Mapper.parseObject(categoryRepository.save(category), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findById(category.getId())).withSelfRel());

        logger.info("Success created category");

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<CategoryDTO> updateCategory(String id, CategoryDTO categoryDTO) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if(categoryAlreadyExist(categoryDTO.getName()) && !categoryDTO.getName().equals(category.getName())){
            throw new CategoryAlreadyExistException("This category already exist");
        }

        categoryDTO.setProducts(category.getProducts());
        categoryDTO.setCreateAt(category.getCreateAt());
        category = Mapper.parseObject(categoryDTO, CategoryDomain.class);
        category.setId(id);

        var dto = Mapper.parseObject(categoryRepository.save(category), CategoryDTO.class)
                .add(linkTo(methodOn(CategoryController.class).findById(id)).withSelfRel());

        logger.info("Success updated category");

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<?> deteleCategory(String id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);

        logger.info("Success deleted category");

        return ResponseEntity.noContent().build();
    }

    private Boolean categoryAlreadyExist(String name){
        return categoryRepository.findByName(name).isPresent();
    }
}
