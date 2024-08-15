package br.com.majo.productservice.services;

import br.com.majo.productservice.controllers.ProductController;
import br.com.majo.productservice.domains.ProductDomain;
import br.com.majo.productservice.dtos.ProductDTO;
import br.com.majo.productservice.infra.cache.ProductCache;
import br.com.majo.productservice.infra.exceptions.ProductAlreadyExistException;
import br.com.majo.productservice.infra.exceptions.ProductNotFoundException;
import br.com.majo.productservice.infra.message.producer.ProductProducer;
import br.com.majo.productservice.infra.util.Mapper;
import br.com.majo.productservice.infra.util.MethodType;
import br.com.majo.productservice.repositories.ProductRepository;
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
public class ProductService {

    @Autowired
    private ProductProducer producer;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCacheService productCacheService;

    public ResponseEntity<List<ProductDTO>> findAll(){
        log.info("Finding all products");

        var productsDTO = Mapper.parseListObject(productRepository.findAll(), ProductDTO.class);

        productsDTO.forEach(x -> x.add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel()));

        return ResponseEntity.ok(productsDTO);
    }

    public ResponseEntity<ProductDTO> findByIdAndRestaurantId(String id, UUID restaurantId){
        log.info("Finding product by id. (product id: ({}))", id);

        var productDTO = Mapper.parseObject(productRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")), ProductDTO.class);

        productDTO.add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(id, restaurantId)).withSelfRel());

        return ResponseEntity.ok(productDTO);
    }

    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO, Boolean isRollback){

        if(productAlreadyExist(productDTO.getName(), productDTO.getCategoryId())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        var product = Mapper.parseObject(productDTO, ProductDomain.class);
        product.setCreateAt(new Date());

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(product.getId(), product.getRestaurantId())).withSelfRel());

        productCacheService.saveLastVersion(Mapper.parseObject(product, ProductCache.class));

        if(!isRollback){
            var trackingId = generateTrackingId();
            dto.setTrackingId(trackingId);
            producer.sendMessageToTracking("PENDING", "Publishing a Product", trackingId);
            producer.sendMessageToCategory(MethodType.CREATE, productDTO.getCategoryId(), dto, dto.getRestaurantId(), trackingId);
            log.info("Success created product. (product id: ({}))", dto.getId());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
    }

    public ResponseEntity<ProductDTO> updateProduct(String id, ProductDTO productDTO, Boolean isRollback){

        var product = productRepository.findByIdAndRestaurantId(id, productDTO.getRestaurantId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productCacheService.saveLastVersion(Mapper.parseObject(product, ProductCache.class));

        if(productAlreadyExist(productDTO.getName(), product.getCategoryId()) &&
                !product.getName().equalsIgnoreCase(productDTO.getName())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        productDTO.setCreateAt(product.getCreateAt());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setSoldOut(product.getSoldOut());
        productDTO.setId(id);
        product = Mapper.parseObject(productDTO, ProductDomain.class);

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(product.getId(), product.getRestaurantId())).withSelfRel());

        if(!isRollback){
            var trackingId = generateTrackingId();
            dto.setTrackingId(trackingId);
            producer.sendMessageToTracking("PENDING", "Updating a Product", trackingId);
            producer.sendMessageToCategory(MethodType.UPDATE, dto, dto.getRestaurantId(), trackingId);
            log.info("Success updated product. (product id: ({}))", id);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
    }

    public ResponseEntity<?> deleteProduct(String id, UUID restaurantId, Boolean isRollback){

        var product = productRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productCacheService.saveLastVersion(Mapper.parseObject(product, ProductCache.class));

        productRepository.delete(product);

        var trackingId = generateTrackingId();

        if(!isRollback){
            producer.sendMessageToTracking("PENDING", "Deleting a Product", trackingId);
            producer.sendMessageToCategory(MethodType.DELETE, product, restaurantId, trackingId);
            log.info("Success deleted product. (product id: ({}))", id);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(trackingId);
    }

    public ResponseEntity<?> updateSoldOut(String id, UUID restaurantId, Boolean soldOut, Boolean isRollback){

        productRepository.updateSoldOut(id, restaurantId, soldOut);

        productCacheService.saveLastVersion(id, soldOut, restaurantId);

        var trackingId = generateTrackingId();

        if(!isRollback){
            producer.sendMessageToTracking("PENDING", "Updating the Sold Out Status", trackingId);
            producer.sendMessageToCategory(MethodType.UPDATE_SOLD_OUT_STATUS, id, soldOut, restaurantId, trackingId);
            log.info("sold out status has been updated success. (product id: ({}))", id);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(trackingId);
    }

    public void updateImageUrl(String id, UUID restaurantId, String imageUrl, Boolean isRollback, String trackingId){

        productRepository.updateImageUrl(id, restaurantId, imageUrl);

        productCacheService.saveLastVersion(id, imageUrl, restaurantId);

        if(!isRollback){
            producer.sendMessageToTracking("PROCESSING", trackingId);
            producer.sendMessageToCategory(MethodType.UPDATE_URL_IMAGE, id, imageUrl, restaurantId, trackingId);
            log.info("image url has been updated success. (product id: ({}))", id);
        }
    }

    public ResponseEntity<?> updateProductCategory(String productId, String categoryId, UUID restaurantId, Boolean isRollback){

        var product = productRepository.findByIdAndRestaurantId(productId, restaurantId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productCacheService.saveLastVersion(Mapper.parseObject(product, ProductCache.class));

        productRepository.updateCategoryId(productId, restaurantId, categoryId);

        var trackingId = generateTrackingId();

        if(!isRollback){
            producer.sendMessageToTracking("PENDING", "Updating the Category of Product", trackingId);
            producer.sendMessageToCategory(MethodType.UPDATE_CATEGORY_ID, categoryId,
                    Mapper.parseObject(product, ProductDTO.class), restaurantId, trackingId);
            log.info("category has been updated success. (product id: ({}))", productId);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(trackingId);
    }

    public ResponseEntity<List<ProductDTO>> findAllByRestaurant(UUID restaurantId) {
        log.info("Finding all products by restaurant");

        var dtos = Mapper.parseListObject(productRepository.findAllByRestaurantId(restaurantId), ProductDTO.class);

        dtos.forEach((x -> x.add(linkTo(methodOn(ProductController.class)
                .findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel())));

        return ResponseEntity.ok(dtos);
    }

    private boolean productAlreadyExist(String name, String categoryId){
        return productRepository.findByNameAndCategoryId(name, categoryId).isPresent();
    }

    private String generateTrackingId(){
        return UUID.randomUUID().toString();
    }
}
