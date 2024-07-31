package br.com.majo.microservice_product.infra.message.producer;

import br.com.majo.microservice_product.infra.util.MethodType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class ProductProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
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
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            logger.info("Json error: " + e.getMessage());
        }
    }

    public void sendMessageToCategory(MethodType methodType, Object object, UUID restaurantId){
        try{
            data.put("object", object);
            data.put("restaurantId", restaurantId);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            logger.info("Json error: " + e.getMessage());
        }
    }
}
