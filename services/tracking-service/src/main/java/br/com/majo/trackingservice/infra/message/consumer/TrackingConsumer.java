package br.com.majo.trackingservice.infra.message.consumer;

import br.com.majo.trackingservice.infra.util.Mapper;
import br.com.majo.trackingservice.infra.util.TrackingListener;
import br.com.majo.trackingservice.services.TrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TrackingConsumer {

    @Autowired
    private TrackingService trackingService;

    @TrackingListener(topics = "${topic.tracking.name}")
    public void consumerFromProduct(@Header(KafkaHeaders.RECEIVED_KEY) String status, Map<String, Object> data) {
        try {
            var description = objectMapping(data.getOrDefault("description", ""), String.class);
            var fromService = objectMapping(data.getOrDefault("fromService", ""), String.class);
            var trackingId = objectMapping(data.getOrDefault("trackingId", ""), String.class);

            if(status.equals("PENDING")){
                trackingService.createTracking(fromService, description, trackingId);
            } else{
                trackingService.updateTrackingStatus(trackingId, status);
            }

        } catch (KafkaException e) {
            log.info("Kafka Consumer Error: {}", e.getMessage());
        }
    }

    private <T> T objectMapping(Object object, Class<T> classType) {
        return Mapper.parseObject(object, classType);
    }

}
