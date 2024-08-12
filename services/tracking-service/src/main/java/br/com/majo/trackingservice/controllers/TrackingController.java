package br.com.majo.trackingservice.controllers;

import br.com.majo.trackingservice.dtos.TrackingDTO;
import br.com.majo.trackingservice.services.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping
    public ResponseEntity<List<TrackingDTO>> findAll(){
        return ResponseEntity.ok(trackingService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TrackingDTO> findById(@PathVariable(value = "id") String id){
        return trackingService.findById(id);
    }

}
