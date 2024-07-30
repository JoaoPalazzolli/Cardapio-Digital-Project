package br.com.majo.category_microservice.repositories;

import br.com.majo.category_microservice.domains.CategoryDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends MongoRepository<CategoryDomain, String> {

    @Query(" { 'name': ?0, 'restaurantId': ?1 } ")
    Optional<CategoryDomain> findByNameAndRestaurantId(String name, UUID restaurantId);

    @Query("{ 'products._id': ?0 }")
    Optional<CategoryDomain> findByProductId(String id);

    @Query(" { 'restaurantId': ?0 } ")
    List<CategoryDomain> findAllByRestaurantId(UUID restaurantId);
}
