package br.com.majo.uploadservice.services;

import br.com.majo.uploadservice.infra.message.producer.UploadProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UploadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AbstractLocalUploadImageService abstractLocalUploadImageService;

    @Autowired
    private UploadProducer producer;

    public ResponseEntity<?> uploadProductImage(String productId, UUID restaurantId, MultipartFile image) {
        String urlImage = abstractLocalUploadImageService.upload(image);

        producer.sendMessageToProduct(productId, restaurantId, urlImage);
        logger.info("Image upload successful -> {}", urlImage);

        return ResponseEntity.noContent().build();
    }

}
