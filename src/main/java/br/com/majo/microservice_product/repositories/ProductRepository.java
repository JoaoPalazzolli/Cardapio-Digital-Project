package br.com.majo.microservice_product.repositories;

import br.com.majo.microservice_product.domains.ProductDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductDomain, String> {

    Optional<ProductDomain> findByName(String name);

    @Query("{ 'id': ?0 }")
    @Update("{ '$set': { 'soldOff': ?1 }}")
    void updateSoldOff(String id, boolean soldOff);

    @Query("{ 'id': ?0}")
    @Update("{ '$set': { 'urlImage': ?1 }}")
    void updateUrlImage(String id, String urlImage);
}
