package br.com.majo.category_microservice.infra.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class CategoryProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToProduct(String message){

        try{
            kafkaTemplate.send(newTopic.name(), objectMapper.writeValueAsString(message));
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
