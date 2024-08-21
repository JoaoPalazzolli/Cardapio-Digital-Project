package br.com.majo.categoryservice.infra.message.consumer;

import br.com.majo.categoryservice.infra.external.dtos.ProductDTO;
import br.com.majo.categoryservice.infra.external.services.ProductService;
import br.com.majo.categoryservice.infra.message.producer.CategoryProducer;
import br.com.majo.categoryservice.infra.utils.CategoryListener;
import br.com.majo.categoryservice.infra.utils.Mapper;
import br.com.majo.categoryservice.infra.utils.TrackingStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class CategoryConsumer {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryProducer producer;

    @CategoryListener(topics = "${topic.product.name}")
    public void consumerFromProduct(@Header(KafkaHeaders.RECEIVED_KEY) String methodType, Map<String, Object> data){
        var trackingId = objectMapping(data.get("trackingId"), String.class);

        try{
            // CREATE, UPDATE, DELETE, UPDATE_SOLD_OUT_STATUS, UPDATE_URL_IMAGE, UPDATE_CATEGORY_ID
            var id = data.getOrDefault("id", "").toString();
            var object = data.get("object");
            var restaurantId = objectMapping(data.get("restaurantId"), UUID.class);

            producer.sendMessageToTracking(TrackingStatus.PROCESSING, trackingId);

            switch (methodType){
                case "CREATE":{
                    log.info("product being added to the category");

                    productService.addProductInCategory(id, objectMapping(object, ProductDTO.class), restaurantId, trackingId);
                    break;
                }
                case "UPDATE":{
                    log.info("product being updated to the category");

                    productService.updateProductInCategory(objectMapping(object, ProductDTO.class), restaurantId, trackingId);
                    break;
                }
                case "DELETE":{
                    log.info("product being deleted to the category");

                    productService.deleteProductInCategory(objectMapping(object, ProductDTO.class), restaurantId, trackingId);
                    break;
                }
                case "UPDATE_SOLD_OUT_STATUS":{
                    log.info("sold out status being updated to the category");

                    productService.updateSoldOutStatusInCategory(id, objectMapping(object, Boolean.class), restaurantId, trackingId);
                    break;
                }
                case "UPDATE_URL_IMAGE":{
                    log.info("image url being updated to the category");

                    productService.updateImageUrlInCategory(id, objectMapping(object, String.class), restaurantId, trackingId);
                    break;
                }
                case "UPDATE_CATEGORY_ID":{
                    log.info("product category being updated");

                    productService.updateProductCategory(id, objectMapping(object, ProductDTO.class), restaurantId, trackingId);
                    break;
                }
                default:{
                    break;
                }
            }

        } catch (KafkaException e){
            producer.sendMessageToTracking(TrackingStatus.FAILED, trackingId);
            log.info("Kafka Consumer Error: {}", e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType){
        return Mapper.parseObject(object, classType);
    }

}
