package br.com.majo.categoryservice.repositories;

import br.com.majo.categoryservice.domains.CategoryDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends MongoRepository<CategoryDomain, String> {

    @Query(" { 'name': ?0, 'restaurantId': ?1 } ")
    Optional<CategoryDomain> findByNameAndRestaurantId(String name, UUID restaurantId);

    @Query("{ 'products._id': ?0, 'restaurantId': ?1 }")
    Optional<CategoryDomain> findByProductIdAndRestaurantId(String id, UUID restaurantId);

    @Query(" { 'restaurantId': ?0 } ")
    List<CategoryDomain> findAllByRestaurantId(UUID restaurantId);

    @Query(" { 'id': ?0, 'restaurantId': ?1 } ")
    Optional<CategoryDomain> findByIdAndRestaurantId(String id, UUID restaurantId);
}
