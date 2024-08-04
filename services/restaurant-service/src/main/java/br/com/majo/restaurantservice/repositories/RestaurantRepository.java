package br.com.majo.restaurantservice.repositories;

import br.com.majo.restaurantservice.domains.RestaurantDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantDomain, UUID> {
}
