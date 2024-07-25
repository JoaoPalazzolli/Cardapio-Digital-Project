package br.com.majo.microservice_product.infra.util;

import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener
public @interface ProductListener {

    @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
    String groupId() default "${topic.product.group-id}";

    @AliasFor(annotation = KafkaListener.class, attribute = "topics")
    String topics() default "";
}
