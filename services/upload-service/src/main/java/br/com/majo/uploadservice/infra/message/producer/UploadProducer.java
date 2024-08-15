package br.com.majo.uploadservice.infra.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class UploadProducer {

    private final Map<String, Object> data = new HashMap<>();

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String fromService;

    public void sendMessageToProduct(String productId, UUID restaurantId, String imageUrl, String trackingId){
        try{
            data.put("productId", productId);
            data.put("restaurantId", restaurantId);
            data.put("imageUrl", imageUrl);
            data.put("trackingId", trackingId);

            kafkaTemplate.send(newTopic.name(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("Kafka produce error: {}", e.getMessage());
        }
    }

    public void sendMessageToTracking(String status, String description, String trackingId){
        try{
            data.put("trackingId", trackingId);
            data.put("description", description);
            data.put("fromService", fromService);

            kafkaTemplate.send("tracking.request.topic.v1", status, objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("Kafka produce error: {}", e.getMessage());
        }
    }
}
