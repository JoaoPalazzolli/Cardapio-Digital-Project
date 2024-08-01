package br.com.majo.microservice_product.repositories;

import br.com.majo.microservice_product.infra.cache.ProductCache;
import org.springframework.data.repository.CrudRepository;

public interface ProductCacheRepository extends CrudRepository<ProductCache, String> {
}
