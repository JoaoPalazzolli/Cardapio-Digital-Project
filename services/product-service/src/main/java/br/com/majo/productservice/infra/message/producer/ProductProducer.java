package br.com.majo.productservice.infra.message.producer;

import br.com.majo.productservice.infra.util.MethodType;
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
public class ProductProducer {
    
    private final Map<String, Object> data = new HashMap<>();

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String fromService;

    public void sendMessageToCategory(MethodType methodType, String id, Object object, UUID restaurantId, String trackingId){
        try{
            data.put("id", id);
            data.put("object", object);
            data.put("trackingId", trackingId);
            data.put("restaurantId", restaurantId);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }

    public void sendMessageToCategory(MethodType methodType, Object object, UUID restaurantId, String trackingId){
        try{
            data.put("object", object);
            data.put("trackingId", trackingId);
            data.put("restaurantId", restaurantId);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }

    public void sendMessageToTracking(String status, String description, String trackingId){
        try{
            data.put("trackingId", trackingId);
            data.put("description", description);
            data.put("fromService", fromService);

            kafkaTemplate.send("tracking.request.topic.v1", status, objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }

    public void sendMessageToTracking(String status, String trackingId){
        try{
            data.put("trackingId", trackingId);

            kafkaTemplate.send("tracking.request.topic.v1", status, objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }
}
