package br.com.majo.uploadservice.Controllers;

import br.com.majo.uploadservice.services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/product/{productId}/restaurant/{restaurantId}")
    public ResponseEntity<?> uploadProductImage(@PathVariable(value = "productId") String productId,
                                                @PathVariable(value = "restaurantId") UUID restaurantId,
                                                @RequestParam(value = "image") MultipartFile image){
        return uploadService.uploadProductImage(productId, restaurantId, image);
    }
}
