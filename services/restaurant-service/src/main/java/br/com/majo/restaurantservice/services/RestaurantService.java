package br.com.majo.restaurantservice.services;

import br.com.majo.restaurantservice.controllers.RestaurantController;
import br.com.majo.restaurantservice.domains.RestaurantDomain;
import br.com.majo.restaurantservice.dtos.RestaurantDTO;
import br.com.majo.restaurantservice.infra.exceptions.RestaurantNotFoundException;
import br.com.majo.restaurantservice.infra.utils.Mapper;
import br.com.majo.restaurantservice.repositories.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public ResponseEntity<List<RestaurantDTO>> findAll() {
        log.info("Finding all restaurants");

        var dtos = Mapper.parseListObject(restaurantRepository.findAll(), RestaurantDTO.class);

        dtos.forEach(x -> x.add(linkTo(methodOn(RestaurantController.class).findById(x.getId())).withSelfRel()));

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<RestaurantDTO> findById(UUID id){
        log.info("Finding restaurant by id");

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

        log.info("restaurant created success");

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<?> deleteRestaurant(UUID id) {
        var restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));

        restaurantRepository.delete(restaurant);

        log.info("restaurant deleted success");

        return ResponseEntity.noContent().build();
    }
}
