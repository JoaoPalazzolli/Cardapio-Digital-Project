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
import java.util.logging.Logger;

@Component
public class CategoryConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @CategoryListener(groupId = "${topic.product.consumer.group-id}")
    public void consumerProducts(@Header(KafkaHeaders.RECEIVED_KEY) String methodType, Map<String, Object> data){
        try{
            // CREATE, UPDATE, DELETE, UPDATE_SOLDOFF_STATUS, UPDATE_URL_IMAGE

            var id = data.getOrDefault("id", "").toString();
            var product = Mapper.parseObject(data.get("product"), ProductDTO.class);

            switch (methodType){
                case "CREATE":{
                    logger.info("product being added to the category");

                    productService.addProductInCategory(id, product);
                    break;
                }
                case "UPDATE":{
                    logger.info("product being updated to the category");

                    productService.updateProductInCategory(product);
                    break;
                }
                case "DELETE":{
                    logger.info("product being deleted to the category");

                    productService.deleteProductInCategory(product);
                    break;
                }
                case "UPDATE_SOLDOFF_STATUS":{

                    break;
                }
                case "UPDATE_URL_IMAGE":{

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

}
