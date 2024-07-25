package br.com.majo.upload_service.Controllers;

import br.com.majo.upload_service.services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> uploadProductImage(@PathVariable(value = "productId") String productId,
                                                @RequestParam(value = "image") MultipartFile image){
        return uploadService.uploadProductImage(productId, image);
    }
}
