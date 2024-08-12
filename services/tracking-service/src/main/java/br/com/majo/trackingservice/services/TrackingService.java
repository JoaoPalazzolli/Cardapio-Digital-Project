package br.com.majo.trackingservice.services;

import br.com.majo.trackingservice.controllers.TrackingController;
import br.com.majo.trackingservice.domains.TrackingDomain;
import br.com.majo.trackingservice.dtos.TrackingDTO;
import br.com.majo.trackingservice.infra.exceptions.TrackingNotFoundException;
import br.com.majo.trackingservice.infra.util.Mapper;
import br.com.majo.trackingservice.infra.util.TrackingStatus;
import br.com.majo.trackingservice.repositories.TrackingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    public List<TrackingDTO> findAll() {
        List<TrackingDomain> list =
                new ArrayList<>((Collection<? extends TrackingDomain>) trackingRepository.findAll());

        var dto = Mapper.parseListObject(list, TrackingDTO.class);

        dto.forEach(x -> x.add(linkTo(methodOn(TrackingController.class).findById(x.getId())).withSelfRel()));

        return dto;
    }

    public ResponseEntity<TrackingDTO> findById(String id) {
        var dto = Mapper.parseObject(trackingRepository.findById(id)
                .orElseThrow(() -> new TrackingNotFoundException("tracking not found")), TrackingDTO.class)
                .add(linkTo(methodOn(TrackingController.class).findById(id)).withSelfRel());

        if(dto.getStatus().equals(TrackingStatus.PROCESSING)){
            return ResponseEntity.status(HttpStatus.PROCESSING).body(dto);
        }

        if(dto.getStatus().equals(TrackingStatus.PENDING)){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        }

        if(dto.getStatus().equals(TrackingStatus.FAILED)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
        }

        return ResponseEntity.ok(dto);
    }

    public void createTracking(String fromService, String description, String trackingId){
        var newTracking = TrackingDomain.builder()
                .id(trackingId)
                .createAt(new Date())
                .lastUpdate(new Date())
                .fromService(fromService)
                .status(TrackingStatus.PENDING)
                .description(description)
                .build();

        trackingRepository.save(newTracking);

        log.info("Success created Tracking. (tracking id: ({}))", newTracking.getId());
    }

    public void updateTrackingStatus(String trackingId, String status) {

        var tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new TrackingNotFoundException("tracking not found"));

        if(status.equalsIgnoreCase("PROCESSING")){
            tracking.setStatus(TrackingStatus.PROCESSING);
        } else if (status.equalsIgnoreCase("PUBLISHED")){
            tracking.setStatus(TrackingStatus.PUBLISHED);
        } else{
            tracking.setStatus(TrackingStatus.FAILED);
        }

        tracking.setLastUpdate(new Date());

        trackingRepository.save(tracking);

        log.info("Success updated tracking status. (tracking id: ({}))", trackingId);
    }
}
