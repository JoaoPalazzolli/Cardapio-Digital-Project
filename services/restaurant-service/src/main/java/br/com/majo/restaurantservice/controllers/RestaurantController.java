package br.com.majo.restaurantservice.controllers;

import br.com.majo.restaurantservice.dtos.RestaurantDTO;
import br.com.majo.restaurantservice.services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> findAll(){
        return restaurantService.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RestaurantDTO> findById(@PathVariable(value = "id") UUID id){
        return restaurantService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO){
        return restaurantService.createRestaurant(restaurantDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable(value = "id") UUID id){
        return restaurantService.deleteRestaurant(id);
    }
}
