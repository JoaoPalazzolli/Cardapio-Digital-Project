package br.com.majo.microservice_product.infra.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer {

    @Autowired
    private NewTopic newTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessageToAddProductInCategory(Object product){

        try{
            kafkaTemplate.send(newTopic.name(), objectMapper.writeValueAsString(product));
        } catch (KafkaException e){
            System.out.println("Erro producer kafka: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
