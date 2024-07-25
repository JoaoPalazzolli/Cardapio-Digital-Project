package br.com.majo.microservice_product.infra.message.consumer;

import br.com.majo.microservice_product.infra.util.ProductListener;
import br.com.majo.microservice_product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class ProductConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ProductService productService;

    @ProductListener(topics = "${topic.category.name}")
    public void consumerFromCategory(String message){
        try{
            logger.info(message);
        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }

    @ProductListener(topics = "${topic.upload.name}")
    public void consumerFromUpload(Map<String, String> data){
        try{
            var productId = data.get("productId");
            var urlImage = data.get("urlImage");

            productService.updateUrlImage(productId, urlImage);

        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }
}
