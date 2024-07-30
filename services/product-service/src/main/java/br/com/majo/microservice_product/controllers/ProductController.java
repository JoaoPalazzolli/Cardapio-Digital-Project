package br.com.majo.microservice_product.controllers;

import br.com.majo.microservice_product.dtos.ProductDTO;
import br.com.majo.microservice_product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll(){
        return productService.findAll();
    }

    @GetMapping(value = "/restaurant/{restaurantId}")
    public ResponseEntity<List<ProductDTO>> findAllByRestaurant(@PathVariable(value = "restaurantId") UUID restaurantId){
        return productService.findAllByRestaurant(restaurantId);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable(value = "id") String id){
        return productService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO){
        return productService.createProduct(productDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable(value = "id") String id,
                                                    @RequestBody ProductDTO productDTO){
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") String id){
        return productService.deleteProduct(id);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateSoldOut(@PathVariable(value = "id") String id,
            @RequestParam(value = "soldOut", defaultValue = "false") Boolean soldOut){
        return productService.updateSoldOut(id, soldOut);
    }

    @PatchMapping(value = "/{productId}/{categoryId}")
    public ResponseEntity<?> updateProductCategory(@PathVariable(value = "productId") String productId,
                                           @PathVariable(value = "categoryId") String categoryId){
        return productService.updateProductCategory(productId, categoryId);
    }

}
