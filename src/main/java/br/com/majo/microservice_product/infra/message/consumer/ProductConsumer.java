package br.com.majo.microservice_product.infra.message.consumer;

import br.com.majo.microservice_product.controllers.ProductController;
import br.com.majo.microservice_product.infra.util.ProductListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Component;

@Component
public class ProductConsumer {

    @Autowired
    private ProductController productController;

    @ProductListener(groupId = "${topic.product.consumer.group-id}")
    public void consumerProducts(String message){
        try{
            System.out.println("Messagem " + message);
        } catch (KafkaException e){
            System.out.println("ERRO ao consumir kafka: " + e);
        }
    }
}
