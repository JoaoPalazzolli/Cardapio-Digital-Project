package br.com.majo.productservice.infra.message.consumer;

import br.com.majo.productservice.infra.util.Mapper;
import br.com.majo.productservice.infra.util.ProductListener;
import br.com.majo.productservice.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ProductConsumer {

    @Autowired
    private ProductService productService;

    @ProductListener(topics = "${topic.category.name}")
    public void consumerFromCategory(@Header(KafkaHeaders.RECEIVED_KEY) String status, String message){
        try{
            switch (status){
                case "SUCCESS":{
                    log.info("SUCCESS - {}", message);
                    break;
                }
                case "FAILED":{
                    log.info("ERROR - {}", message);
                    break;
                }
            }

        } catch (KafkaException e){
            log.info("Kafka Consumer Error: {}", e.getMessage());
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
            log.info("Kafka Consumer Error: {}", e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType){
        return Mapper.parseObject(object, classType);
    }
}
