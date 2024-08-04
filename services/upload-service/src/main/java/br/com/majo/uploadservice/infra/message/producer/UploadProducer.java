package br.com.majo.uploadservice.infra.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class UploadProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Map<String, Object> data = new HashMap<>();

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToProduct(String productId, UUID restaurantId, String urlImage){
        try{
            data.put("productId", productId);
            data.put("restaurantId", restaurantId);
            data.put("urlImage", urlImage);

            kafkaTemplate.send(newTopic.name(), objectMapper.writeValueAsString(data));
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
