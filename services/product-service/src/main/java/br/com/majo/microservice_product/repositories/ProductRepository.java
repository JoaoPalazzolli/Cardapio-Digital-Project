package br.com.majo.microservice_product.repositories;

import br.com.majo.microservice_product.domains.ProductDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends MongoRepository<ProductDomain, String> {

    @Query("{ 'name': ?0, 'categoryId': ?1 }")
    Optional<ProductDomain> findByNameAndCategoryId(String name, String categoryId);

    @Query("{ 'id': ?0 }")
    @Update("{ '$set': { 'soldOut': ?1 }}")
    void updateSoldOut(String id, boolean soldOut);

    @Query("{ 'id': ?0}")
    @Update("{ '$set': { 'urlImage': ?1 }}")
    void updateUrlImage(String id, String urlImage);

    @Query("{ 'id': ?0}")
    @Update("{ '$set': { 'categoryId': ?1 }}")
    void updateCategoryId(String productId, String categoryId);

    @Query("{ 'restaurantId': ?0 }")
    List<ProductDomain> findAllByRestaurantId(UUID id);
}
