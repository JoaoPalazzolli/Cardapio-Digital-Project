package br.com.majo.category_microservice.infra.external.services;

import br.com.majo.category_microservice.infra.exceptions.CategoryNotFoundException;
import br.com.majo.category_microservice.infra.external.dtos.ProductDTO;
import br.com.majo.category_microservice.infra.message.producer.CategoryProducer;
import br.com.majo.category_microservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.logging.Logger;

@Service
public class ProductService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryProducer producer;

    @Transactional
    public void addProductInCategory(String categoryId, ProductDTO productDTO){
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct("Category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        if (category.getProducts() == null){
            category.setProducts(Collections.singletonList(productDTO));
        } else{
            category.getProducts().add(productDTO);
        }

        categoryRepository.save(category);

        producer.sendMessageToProduct("Product success added to category");
        logger.info("product success added to category");
    }

    public void updateProductInCategory(ProductDTO productDTO){
        var category = categoryRepository.findByProductId(productDTO.getId())
                .orElseThrow(() -> {
                    producer.sendMessageToProduct("Category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        if(category.getProducts() == null){
            throw new RuntimeException("There are no products in this category");
        }

        var product = category.getProducts().stream().filter(x -> x.getId().equals(productDTO.getId())).toList().get(0);
        var indexProduct = category.getProducts().indexOf(product);

        category.getProducts().add(indexProduct, productDTO);
        category.getProducts().remove(product);

        categoryRepository.save(category);

        producer.sendMessageToProduct("Product success updated to category");
        logger.info("product success updated to category");

    }

    public void deleteProductInCategory(ProductDTO productDTO){
        var category = categoryRepository.findByProductId(productDTO.getId())
                .orElseThrow(() -> {
                    producer.sendMessageToProduct("Category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        if(category.getProducts() == null){
            throw new RuntimeException("There are no products in this category");
        }

        category.getProducts().removeIf(x -> x.getId().equals(productDTO.getId()));

        categoryRepository.save(category);

        producer.sendMessageToProduct("Product success deleted to category");
        logger.info("product success deleted to category");

    }
}
