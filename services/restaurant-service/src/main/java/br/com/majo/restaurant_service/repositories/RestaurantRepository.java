package br.com.majo.restaurant_service.repositories;

import br.com.majo.restaurant_service.domains.RestaurantDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantDomain, UUID> {
}
