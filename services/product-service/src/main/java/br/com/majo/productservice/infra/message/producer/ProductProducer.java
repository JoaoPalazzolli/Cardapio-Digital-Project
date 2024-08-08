package br.com.majo.productservice.infra.message.producer;

import br.com.majo.productservice.infra.util.MethodType;
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
public class ProductProducer {
    
    private final Map<String, Object> data = new HashMap<>();

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToCategory(MethodType methodType, String id, Object object, UUID restaurantId){
        try{
            data.put("id", id);
            data.put("object", object);
            data.put("restaurantId", restaurantId);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }

    public void sendMessageToCategory(MethodType methodType, Object object, UUID restaurantId){
        try{
            data.put("object", object);
            data.put("restaurantId", restaurantId);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("product producer error: {}", e.getMessage());
        }
    }
}
