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
import java.util.logging.Logger;

@Component
public class ProductProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToCategory(MethodType methodType, String categoryId, Object product){
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("id", categoryId);
            data.put("product", product);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            logger.info("Json error: " + e.getMessage());
        }
    }

    public void sendMessageToCategory(MethodType methodType, Object product){
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("product", product);

            kafkaTemplate.send(newTopic.name(), methodType.toString(), objectMapper.writeValueAsString(data));
        } catch (KafkaException e){
            logger.info("Kafka produce error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            logger.info("Json error: " + e.getMessage());
        }
    }
}
