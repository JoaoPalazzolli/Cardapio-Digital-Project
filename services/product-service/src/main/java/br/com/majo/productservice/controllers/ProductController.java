package br.com.majo.productservice.controllers;

import br.com.majo.productservice.dtos.ProductDTO;
import br.com.majo.productservice.services.ProductService;
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

    @GetMapping(value = "/{id}/restaurant/{restaurantId}")
    public ResponseEntity<ProductDTO> findByIdAndRestaurantId(@PathVariable(value = "id") String id,
                                                              @PathVariable(value = "restaurantId") UUID restaurantId){
        return productService.findByIdAndRestaurantId(id, restaurantId);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO){
        return productService.createProduct(productDTO, false);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable(value = "id") String id,
                                                    @RequestBody ProductDTO productDTO){
        return productService.updateProduct(id, productDTO, false);
    }

    @DeleteMapping(value = "/{id}/restaurant/{restaurantId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") String id,
                                           @PathVariable(value = "restaurantId") UUID restaurantId){
        return productService.deleteProduct(id, restaurantId, false);
    }

    @PatchMapping(value = "/{id}/restaurant/{restaurantId}")
    public ResponseEntity<?> updateSoldOut(@PathVariable(value = "id") String id,
            @PathVariable(value = "restaurantId") UUID restaurantId,
            @RequestParam(value = "soldOut", defaultValue = "false") Boolean soldOut){
        return productService.updateSoldOut(id, restaurantId, soldOut, false);
    }

    @PatchMapping(value = "/{productId}/category/{categoryId}/restaurant/{restaurantId}")
    public ResponseEntity<?> updateProductCategory(@PathVariable(value = "productId") String productId,
                                           @PathVariable(value = "categoryId") String categoryId,
                                                   @PathVariable(value = "restaurantId") UUID restaurantId){
        return productService.updateProductCategory(productId, categoryId, restaurantId, false);
    }

}
