package br.com.majo.productservice.infra.message.consumer;

import br.com.majo.productservice.dtos.ProductDTO;
import br.com.majo.productservice.infra.exceptions.ProductNotFoundException;
import br.com.majo.productservice.infra.util.Mapper;
import br.com.majo.productservice.infra.util.ProductListener;
import br.com.majo.productservice.repositories.ProductCacheRepository;
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

    @Autowired
    private ProductCacheRepository productCacheRepository;

    @ProductListener(topics = "${topic.category.name}")
    public void consumerFromCategory(@Header(KafkaHeaders.RECEIVED_KEY) String status, Map<String, Object> data) {
        //  CREATE, UPDATE, DELETE, UPDATE_SOLD_OUT_STATUS, UPDATE_URL_IMAGE, UPDATE_CATEGORY_ID
        try {
            if(status.equalsIgnoreCase("SUCCESS")){
                log.info("SUCCESS - {}", data.get("message"));
            } else {
                var rollbackMethod = data.get("rollback").toString();
                var productId = data.get("productId").toString();

                var lastProduct = productCacheRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("product not found"));

                switch (rollbackMethod) {
                    case "CREATE": {
                        productService.deleteProduct(lastProduct.getId(), lastProduct.getRestaurantId(), true);
                        break;
                    }
                    case "UPDATE": {
                        productService.updateProduct(lastProduct.getId(), Mapper.parseObject(lastProduct, ProductDTO.class), true);
                        break;
                    }
                    case "DELETE": {
                        productService.createProduct(Mapper.parseObject(lastProduct, ProductDTO.class), true);
                        break;
                    }
                    case "UPDATE_SOLD_OUT_STATUS": {
                        productService.updateSoldOut(lastProduct.getId(), lastProduct.getRestaurantId(), lastProduct.getSoldOut(), true);
                        break;
                    }
                    case "UPDATE_URL_IMAGE": {
                        productService.updateUrlImage(lastProduct.getId(), lastProduct.getRestaurantId(), lastProduct.getUrlImage(), true);
                        break;
                    }
                    case "UPDATE_CATEGORY_ID": {
                        productService.updateProductCategory(lastProduct.getId(), lastProduct.getCategoryId(), lastProduct.getRestaurantId(), true);
                        break;
                    }
                }
                log.info("There was an error in the category service and a rollback was performed");
            }
        } catch (KafkaException e) {
            log.info("Kafka Consumer Error: {}", e.getMessage());
        }
    }

    @ProductListener(topics = "${topic.upload.name}")
    public void consumerFromUpload(Map<String, Object> data) {
        try {
            var productId = objectMapping(data.get("productId"), String.class);
            var restaurantId = objectMapping(data.get("restaurantId"), UUID.class);
            var urlImage = objectMapping(data.get("urlImage"), String.class);

            productService.updateUrlImage(productId, restaurantId, urlImage, false);

        } catch (KafkaException e) {
            log.info("Kafka Consumer Error: {}", e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType) {
        return Mapper.parseObject(object, classType);
    }
}
