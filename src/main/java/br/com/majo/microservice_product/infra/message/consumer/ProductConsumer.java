package br.com.majo.microservice_product.infra.message.consumer;

import br.com.majo.microservice_product.controllers.ProductController;
import br.com.majo.microservice_product.infra.util.ProductListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ProductConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ProductController productController;

    @ProductListener(groupId = "${topic.product.consumer.group-id}")
    public void consumerProducts(String message){
        try{
            logger.info("Messagem: " + message);
        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }
}
