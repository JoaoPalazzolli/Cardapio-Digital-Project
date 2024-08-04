package br.com.majo.categoryservice.infra.message.producer;

import br.com.majo.categoryservice.infra.utils.StatusMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.application.name}")
    private String fromMessage;

    public void sendMessageToProduct(StatusMessage status, String message){
        try{
            kafkaTemplate.send(newTopic.name(), status.toString(), objectMapper.writeValueAsString(fromMessage + " message: " + message));
        } catch (KafkaException | JsonProcessingException e){
            logger.info("error category producer: " + e.getMessage());
        }
    }
}
