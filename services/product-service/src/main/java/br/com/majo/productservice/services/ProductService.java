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
import br.com.majo.productservice.repositories.ProductCacheRepository;
import br.com.majo.productservice.repositories.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductProducer producer;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCacheRepository productCacheRepository;

    public ResponseEntity<List<ProductDTO>> findAll(){
        logger.info("Finding all products");

        var productsDTO = Mapper.parseListObject(productRepository.findAll(), ProductDTO.class);

        productsDTO.forEach(x -> x.add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel()));

        return ResponseEntity.ok(productsDTO);
    }

    public ResponseEntity<ProductDTO> findByIdAndRestaurantId(String id, UUID restaurantId){
        logger.info("Finding product by id. (product id: ({}))", id);

        var productDTO = Mapper.parseObject(productRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")), ProductDTO.class);

        productDTO.add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(id, restaurantId)).withSelfRel());

        return ResponseEntity.ok(productDTO);
    }

    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO){

        if(productAlreadyExist(productDTO.getName(), productDTO.getCategoryId())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        var product = Mapper.parseObject(productDTO, ProductDomain.class);
        product.setCreateAt(new Date());

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(product.getId(), product.getRestaurantId())).withSelfRel());

        producer.sendMessageToCategory(MethodType.CREATE, productDTO.getCategoryId(), dto, dto.getRestaurantId());
        logger.info("Success created product. (product id: ({}))", dto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    public ResponseEntity<ProductDTO> updateProduct(String id, ProductDTO productDTO){

        var product = productRepository.findByIdAndRestaurantId(id, productDTO.getRestaurantId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productCacheRepository.save(Mapper.parseObject(product, ProductCache.class)); // armazenando antigo produto no cache

        if(productAlreadyExist(productDTO.getName(), product.getCategoryId()) &&
                !product.getName().equalsIgnoreCase(productDTO.getName())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        productDTO.setCreateAt(product.getCreateAt());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setUrlImage(product.getUrlImage());
        productDTO.setSoldOut(product.getSoldOut());
        productDTO.setId(id);
        product = Mapper.parseObject(productDTO, ProductDomain.class);

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findByIdAndRestaurantId(product.getId(), product.getRestaurantId())).withSelfRel());

        producer.sendMessageToCategory(MethodType.UPDATE, dto, dto.getRestaurantId());
        logger.info("Success updated product. (product id: ({}))", id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> deleteProduct(String id, UUID restaurantId){

        var product = productRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.delete(product);

        producer.sendMessageToCategory(MethodType.DELETE, product, restaurantId);
        logger.info("Success deleted product. (product id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> updateSoldOut(String id, UUID restaurantId, Boolean soldOut){

        productRepository.updateSoldOut(id, restaurantId, soldOut);

        producer.sendMessageToCategory(MethodType.UPDATE_SOLD_OUT_STATUS, id, soldOut, restaurantId);
        logger.info("sold out status has been updated success. (product id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    public void updateUrlImage(String id, UUID restaurantId, String urlImage){

        productRepository.updateUrlImage(id, restaurantId, urlImage);

        producer.sendMessageToCategory(MethodType.UPDATE_URL_IMAGE, id, urlImage, restaurantId);
        logger.info("url image has been updated success. (product id: ({}))", id);
    }

    public ResponseEntity<?> updateProductCategory(String productId, String categoryId, UUID restaurantId){

        var product = productRepository.findByIdAndRestaurantId(productId, restaurantId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.updateCategoryId(productId, restaurantId, categoryId);

        producer.sendMessageToCategory(MethodType.UPDATE_CATEGORY_ID, categoryId,
                Mapper.parseObject(product, ProductDTO.class), restaurantId);
        logger.info("category has been updated success. (product id: ({}))", productId);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<ProductDTO>> findAllByRestaurant(UUID restaurantId) {
        var dtos = Mapper.parseListObject(productRepository.findAllByRestaurantId(restaurantId), ProductDTO.class);

        dtos.forEach((x -> x.add(linkTo(methodOn(ProductController.class)
                .findByIdAndRestaurantId(x.getId(), x.getRestaurantId())).withSelfRel())));

        return ResponseEntity.ok(dtos);
    }

    private boolean productAlreadyExist(String name, String categoryId){
        return productRepository.findByNameAndCategoryId(name, categoryId).isPresent();
    }
}
