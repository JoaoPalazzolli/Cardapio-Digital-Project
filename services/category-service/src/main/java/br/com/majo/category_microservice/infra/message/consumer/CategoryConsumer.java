package br.com.majo.category_microservice.infra.message.consumer;

import br.com.majo.category_microservice.infra.external.dtos.ProductDTO;
import br.com.majo.category_microservice.infra.external.services.ProductService;
import br.com.majo.category_microservice.infra.utils.CategoryListener;
import br.com.majo.category_microservice.infra.utils.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class CategoryConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @CategoryListener(topics = "${topic.product.name}")
    public void consumerFromProduct(@Header(KafkaHeaders.RECEIVED_KEY) String methodType, Map<String, Object> data){
        try{
            // CREATE, UPDATE, DELETE, UPDATE_SOLD_OUT_STATUS, UPDATE_URL_IMAGE, UPDATE_CATEGORY_ID

            var id = data.getOrDefault("id", "").toString();
            var object = data.get("object");
            var restaurantId = objectMapping(data.get("restaurantId"), UUID.class);

            switch (methodType){
                case "CREATE":{
                    logger.info("product being added to the category");

                    productService.addProductInCategory(id, objectMapping(object, ProductDTO.class), restaurantId);
                    break;
                }
                case "UPDATE":{
                    logger.info("product being updated to the category");

                    productService.updateProductInCategory(objectMapping(object, ProductDTO.class), restaurantId);
                    break;
                }
                case "DELETE":{
                    logger.info("product being deleted to the category");

                    productService.deleteProductInCategory(objectMapping(object, ProductDTO.class), restaurantId);
                    break;
                }
                case "UPDATE_SOLD_OUT_STATUS":{
                    logger.info("sold out status being updated to the category");

                    productService.updateSoldOutStatusInCategory(id, objectMapping(object, Boolean.class), restaurantId);
                    break;
                }
                case "UPDATE_URL_IMAGE":{
                    logger.info("url image being updated to the category");

                    productService.updateUrlImageInCategory(id, objectMapping(object, String.class), restaurantId);
                    break;
                }
                case "UPDATE_CATEGORY_ID":{
                    logger.info("product category being updated");

                    productService.updateProductCategory(id, objectMapping(object, ProductDTO.class), restaurantId);
                    break;
                }
                default:{
                    break;
                }
            }

        } catch (KafkaException e){
            logger.info("Kafka Consumer Error: " + e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType){
        return Mapper.parseObject(object, classType);
    }

}
