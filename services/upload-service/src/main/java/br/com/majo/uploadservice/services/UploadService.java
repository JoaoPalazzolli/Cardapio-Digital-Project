package br.com.majo.uploadservice.services;

import br.com.majo.uploadservice.infra.message.producer.UploadProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class UploadService {

    @Autowired
    private AbstractLocalUploadImageService abstractLocalUploadImageService;

    @Autowired
    private UploadProducer producer;

    public ResponseEntity<?> uploadProductImage(String productId, UUID restaurantId, MultipartFile image) {
        String imageUrl = abstractLocalUploadImageService.upload(image);

        var trackingId = generateTrackingId();

        producer.sendMessageToTracking("PENDING", "updating the product image url", trackingId);
        producer.sendMessageToProduct(productId, restaurantId, imageUrl, trackingId);
        log.info("Image upload successful -> {}", imageUrl);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(trackingId);
    }

    private String generateTrackingId(){
        return UUID.randomUUID().toString();
    }

}
