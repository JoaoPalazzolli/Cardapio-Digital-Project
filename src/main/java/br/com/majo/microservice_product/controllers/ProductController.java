package br.com.majo.microservice_product.controllers;

import br.com.majo.microservice_product.dtos.ProductDTO;
import br.com.majo.microservice_product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll(){
        return productService.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable(value = "id") String id){
        return productService.findById(id);
    }

    @PostMapping(value = "/{categoryId}")
    public ResponseEntity<ProductDTO> createProduct(@PathVariable(value = "categoryId") String categoryId,
                                                    @RequestBody ProductDTO productDTO){
        return productService.createProduct(categoryId, productDTO);
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

}
