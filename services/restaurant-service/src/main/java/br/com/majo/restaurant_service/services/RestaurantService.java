package br.com.majo.restaurant_service.services;

import br.com.majo.restaurant_service.controllers.RestaurantController;
import br.com.majo.restaurant_service.domains.RestaurantDomain;
import br.com.majo.restaurant_service.dtos.RestaurantDTO;
import br.com.majo.restaurant_service.infra.exceptions.RestaurantNotFoundException;
import br.com.majo.restaurant_service.infra.utils.Mapper;
import br.com.majo.restaurant_service.repositories.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public ResponseEntity<List<RestaurantDTO>> findAll() {
        var restaurantsDTO = Mapper.parseListObject(restaurantRepository.findAll(), RestaurantDTO.class)
                .stream().map(x -> x.add(linkTo(methodOn(RestaurantController.class)
                        .findById(x.getId())).withSelfRel())).toList();

        return ResponseEntity.ok(restaurantsDTO);
    }

    public ResponseEntity<RestaurantDTO> findById(UUID id){
        var restaurantDTO = Mapper.parseObject(restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found")), RestaurantDTO.class);

        restaurantDTO.add(linkTo(methodOn(RestaurantController.class).findById(id)).withSelfRel());

        return ResponseEntity.ok(restaurantDTO);
    }

    @Transactional
    public ResponseEntity<RestaurantDTO> createRestaurant(RestaurantDTO restaurantDTO) {

        var restaurant = Mapper.parseObject(restaurantDTO, RestaurantDomain.class);
        restaurant.setCreateAt(new Date());

        var dto = Mapper.parseObject(restaurantRepository.save(restaurant), RestaurantDTO.class)
                .add(linkTo(methodOn(RestaurantController.class).findById(restaurant.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<?> deleteRestaurant(UUID id) {
        var restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));

        restaurantRepository.delete(restaurant);

        return ResponseEntity.noContent().build();
    }
}
