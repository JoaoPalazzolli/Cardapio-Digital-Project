package br.com.majo.microservice_product.services;

import br.com.majo.microservice_product.controllers.ProductController;
import br.com.majo.microservice_product.domains.ProductDomain;
import br.com.majo.microservice_product.dtos.ProductDTO;
import br.com.majo.microservice_product.infra.exceptions.ProductAlreadyExistException;
import br.com.majo.microservice_product.infra.exceptions.ProductNotFoundException;
import br.com.majo.microservice_product.infra.message.producer.ProductProducer;
import br.com.majo.microservice_product.infra.util.Mapper;
import br.com.majo.microservice_product.repositories.ProductRepository;
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
public class ProductService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

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
        logger.info("Finding product by id");

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

        logger.info("Success created product");

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<ProductDTO> updateProduct(String id, ProductDTO productDTO){

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productDTO.setCreateAt(product.getCreateAt());

        if(productAlreadyExist(productDTO.getName()) && !product.getName().equals(productDTO.getName())){
            throw new ProductAlreadyExistException("This product already exist");
        }

        product = Mapper.parseObject(productDTO, ProductDomain.class);
        product.setId(id);

        var dto = Mapper.parseObject(productRepository.save(product), ProductDTO.class)
                .add(linkTo(methodOn(ProductController.class).findById(product.getId())).withSelfRel());

        logger.info("Success updated product");

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<?> deleteProduct(String id){

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.delete(product);

        logger.info("Success deleted product");

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> updateSoldOff(String id, Boolean soldOff){

        productRepository.updateSoldOff(id, soldOff);

        logger.info("success updated sold off status");

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> updateUrlImage(String id, String urlImage){

        productRepository.updateUrlImage(id, urlImage);

        logger.info("success updated url Image");

        return ResponseEntity.noContent().build();
    }

    private boolean productAlreadyExist(String name){
        return productRepository.findByName(name).isPresent();
    }
}
