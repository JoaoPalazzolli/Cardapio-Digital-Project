package br.com.majo.microservice_product.services;

import br.com.majo.microservice_product.controllers.ProductController;
import br.com.majo.microservice_product.domains.ProductDomain;
import br.com.majo.microservice_product.dtos.ProductDTO;
import br.com.majo.microservice_product.infra.exceptions.ProductAlreadyExistException;
import br.com.majo.microservice_product.infra.exceptions.ProductNotFoundException;
import br.com.majo.microservice_product.infra.message.producer.ProductProducer;
import br.com.majo.microservice_product.infra.util.Mapper;
import br.com.majo.microservice_product.infra.util.MethodType;
import br.com.majo.microservice_product.repositories.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductProducer producer;

    @Autowired
    private ProductRepository productRepository;


    public ResponseEntity<List<ProductDTO>> findAll(){
        logger.info("Finding all products");

        var productsDTO = Mapper.parseListObject(productRepository.findAll(), ProductDTO.class);

        productsDTO.forEach(x -> x.add(linkTo(methodOn(ProductController.class).findById(x.getId())).withSelfRel()));

        return ResponseEntity.ok(productsDTO);
    }

    public ResponseEntity<ProductDTO> findById(String id){
        logger.info("Finding product by id. (product id: ({}))", id);

        var productDTO = Mapper.parseObject(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found")), ProductDTO.class);

        productDTO.add(linkTo(methodOn(ProductController.class).findById(id)).withSelfRel());

        return ResponseEntity.ok(productDTO);
    }

    @Transactional
    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO){

        if(productAlreadyExist(productDTO.getName())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        var product = Mapper.parseObject(productDTO, ProductDomain.class);
        product.setCreateAt(new Date());

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findById(product.getId())).withSelfRel());

        producer.sendMessageToCategory(MethodType.CREATE, productDTO.getCategoryId(), dto);
        logger.info("Success created product. (product id: ({}))", dto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<ProductDTO> updateProduct(String id, ProductDTO productDTO){

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productDTO.setCreateAt(product.getCreateAt());

        if(productAlreadyExist(productDTO.getName()) && !product.getName().equalsIgnoreCase(productDTO.getName())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setUrlImage(product.getUrlImage());
        product = Mapper.parseObject(productDTO, ProductDomain.class);
        product.setId(id);

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findById(product.getId())).withSelfRel());

        producer.sendMessageToCategory(MethodType.UPDATE, dto);
        logger.info("Success updated product. (product id: ({}))", id);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<?> deleteProduct(String id){

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.delete(product);

        producer.sendMessageToCategory(MethodType.DELETE, product);
        logger.info("Success deleted product. (product id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> updateSoldOut(String id, Boolean soldOut){

        productRepository.updateSoldOut(id, soldOut);

        producer.sendMessageToCategory(MethodType.UPDATE_SOLD_OUT_STATUS, id, soldOut);
        logger.info("sold out status has been updated success. (product id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> updateUrlImage(String id, String urlImage){

        productRepository.updateUrlImage(id, urlImage);

        producer.sendMessageToCategory(MethodType.UPDATE_URL_IMAGE, id, urlImage);
        logger.info("url image has been updated success. (product id: ({}))", id);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> updateProductCategory(String productId, String categoryId){

        var product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.updateCategoryId(productId, categoryId);

        producer.sendMessageToCategory(MethodType.UPDATE_CATEGORY_ID, categoryId,
                Mapper.parseObject(product, ProductDTO.class));
        logger.info("category has been updated success. (product id: ({}))", productId);

        return ResponseEntity.noContent().build();
    }

    private boolean productAlreadyExist(String name){
        return productRepository.findByName(name).isPresent();
    }
}
