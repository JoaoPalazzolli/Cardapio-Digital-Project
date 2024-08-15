package br.com.majo.productservice.repositories;

import br.com.majo.productservice.domains.ProductDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends MongoRepository<ProductDomain, String> {

    @Query("{ 'name': ?0, 'categoryId': ?1 }")
    Optional<ProductDomain> findByNameAndCategoryId(String name, String categoryId);

    @Query("{ 'id': ?0, 'restaurantId': ?1 }")
    @Update("{ '$set': { 'soldOut': ?2 }}")
    void updateSoldOut(String id, UUID restaurantId, boolean soldOut);

    @Query("{ 'id': ?0, 'restaurantId': ?1 }")
    @Update("{ '$set': { 'urlImage': ?2 }}")
    void updateImageUrl(String id, UUID restaurantId, String urlImage);

    @Query("{ 'id': ?0, 'restaurantId': ?1 }")
    @Update("{ '$set': { 'categoryId': ?2 }}")
    void updateCategoryId(String productId, UUID restaurantId, String categoryId);

    @Query("{ 'restaurantId': ?0 }")
    List<ProductDomain> findAllByRestaurantId(UUID id);

    @Query(" { 'id': ?0, 'restaurantId': ?1 } ")
    Optional<ProductDomain> findByIdAndRestaurantId(String id, UUID restaurantId);
}
