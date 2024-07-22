package br.com.majo.category_microservice.repositories;

import br.com.majo.category_microservice.domains.CategoryDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryDomain, String> {

    Optional<CategoryDomain> findByName(String name);

    @Query("{ 'products._id': ?0 }")
    Optional<CategoryDomain> findByProductId(String id);
}
