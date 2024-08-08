package br.com.majo.categoryservice.infra.message.producer;

import br.com.majo.categoryservice.infra.utils.RollbackMethod;
import br.com.majo.categoryservice.infra.utils.StatusMessage;
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

@Slf4j
@Component
public class CategoryProducer {

    private final Map<String, Object> data = new HashMap<>();

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
            data.put("message", fromMessage + " message: " + message);

            kafkaTemplate.send(newTopic.name(), status.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("error category producer: {}", e.getMessage());
        }
    }

    public void sendMessageToProduct(StatusMessage status, RollbackMethod rollbackMethod, String productId){
        try{
            data.put("rollback", rollbackMethod);
            data.put("productId", productId);

            kafkaTemplate.send(newTopic.name(), status.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException | JsonProcessingException e){
            log.info("error category producer: {}", e.getMessage());
        }
    }
}
