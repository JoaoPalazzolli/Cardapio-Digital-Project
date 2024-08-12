package br.com.majo.trackingservice.infra.message.producer;

import br.com.majo.trackingservice.infra.util.TrackingStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class TrackingProducer {
    
    private final Map<String, Object> data = new HashMap<>();

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToCategory(String productId, TrackingStatus status){
        try{
            kafkaTemplate.send(newTopic.name(), status.toString(), objectMapper.writeValueAsString(productId));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }

}
