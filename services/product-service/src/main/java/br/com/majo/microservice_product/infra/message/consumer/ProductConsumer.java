package br.com.majo.microservice_product.infra.message.consumer;

import br.com.majo.microservice_product.infra.util.Mapper;
import br.com.majo.microservice_product.infra.util.ProductListener;
import br.com.majo.microservice_product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class ProductConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ProductService productService;

    @ProductListener(topics = "${topic.category.name}")
    public void consumerFromCategory(@Header(KafkaHeaders.RECEIVED_KEY) String status, String message){
        try{
            switch (status){
                case "SUCCESS":{
                    logger.info("SUCCESS - " + message);
                    break;
                }
                case "FAILED":{
                    logger.info("ERROR - " + message);
                }
            }

        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }

    @ProductListener(topics = "${topic.upload.name}")
    public void consumerFromUpload(Map<String, Object> data){
        try{
            var productId = objectMapping(data.get("productId"), String.class);
            var restaurantId = objectMapping(data.get("restaurantId"), UUID.class);
            var urlImage = objectMapping(data.get("urlImage"), String.class);

            productService.updateUrlImage(productId, restaurantId, urlImage);

        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType){
        return Mapper.parseObject(object, classType);
    }
}
