package br.com.majo.category_microservice.infra.external.services;

import br.com.majo.category_microservice.infra.exceptions.CategoryNotFoundException;
import br.com.majo.category_microservice.infra.external.dtos.ProductDTO;
import br.com.majo.category_microservice.infra.message.producer.CategoryProducer;
import br.com.majo.category_microservice.infra.utils.StatusMessage;
import br.com.majo.category_microservice.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryProducer producer;

    @Transactional
    public void addProductInCategory(String categoryId, ProductDTO productDTO, UUID restaurantId){
        var category = categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        if (category.getProducts() == null){
            category.setProducts(Collections.singletonList(productDTO));
        }else{
            category.getProducts().add(productDTO);
        }

        categoryRepository.save(category);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("product success added to category. (product id: (%s))", productDTO.getId()));
        logger.info("product success added to category. (product id: ({}))", productDTO.getId());
    }

    @Transactional
    public void updateProductInCategory(ProductDTO productDTO, UUID restaurantId){
        var category = categoryRepository.findByProductIdAndRestaurantId(productDTO.getId(), restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        var product = category.getProducts().stream().filter(x -> x.getId().equals(productDTO.getId())).toList().get(0);
        var indexProduct = category.getProducts().indexOf(product);

        category.getProducts().add(indexProduct, productDTO);
        category.getProducts().remove(product);

        categoryRepository.save(category);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("success updated product in category. (product id: (%s))", productDTO.getId()));
        logger.info("success updated product in category. (product id: ({}))", productDTO.getId());

    }

    @Transactional
    public void deleteProductInCategory(ProductDTO productDTO, UUID restaurantId){
        var category = categoryRepository.findByProductIdAndRestaurantId(productDTO.getId(), restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        category.getProducts().removeIf(x -> x.getId().equals(productDTO.getId()));

        categoryRepository.save(category);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("success deleted product in category. (product id: (%s))", productDTO.getId()));
        logger.info("success deleted product in category. (product id: ({}))", productDTO.getId());

    }

    @Transactional
    public void updateSoldOutStatusInCategory(String productId, boolean soldOut, UUID restaurantId){
        var category = categoryRepository.findByProductIdAndRestaurantId(productId, restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        var product = category.getProducts().stream().filter(x -> x.getId().equals(productId)).toList().get(0);

        product.setSoldOut(soldOut);

        categoryRepository.save(category);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("sold out status has been updated successfully. (product id: (%s))", productId));
        logger.info("sold out status has been updated successfully. (product id: ({}))", productId);

    }

    @Transactional
    public void updateUrlImageInCategory(String productId, String urlImage, UUID restaurantId) {
        var category = categoryRepository.findByProductIdAndRestaurantId(productId, restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        var product = category.getProducts().stream().filter(x -> x.getId().equals(productId)).toList().get(0);

        product.setUrlImage(urlImage);

        categoryRepository.save(category);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("url image has been updated successfully. (product id: (%s))", productId));
        logger.info("url image has been updated successfully. (product id: ({}))", productId);
    }

    @Transactional
    public void updateProductCategory(String categoryId, ProductDTO productDTO, UUID restaurantId){

        var sourceCategory = categoryRepository.findByProductIdAndRestaurantId(productDTO.getId(), restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        sourceCategory.getProducts().removeIf(x -> x.getId().equals(productDTO.getId()));

        var targetCategory = categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> {
                    producer.sendMessageToProduct(StatusMessage.FAILED, "category not found");
                    return new CategoryNotFoundException("Category not found");
                });

        if (targetCategory.getProducts() == null){
            targetCategory.setProducts(Collections.singletonList(productDTO));
        }else{
            targetCategory.getProducts().add(productDTO);
        }

        categoryRepository.save(sourceCategory);
        categoryRepository.save(targetCategory);

        producer.sendMessageToProduct(StatusMessage.SUCCESS, String
                .format("product category has been updated successfully. (product id: (%s))", productDTO.getId()));
        logger.info("product category has been updated successfully. (product id: ({}))", productDTO.getId());
    }
}
