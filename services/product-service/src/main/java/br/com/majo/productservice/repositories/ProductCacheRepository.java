package br.com.majo.productservice.repositories;

import br.com.majo.productservice.infra.cache.ProductCache;
import org.springframework.data.repository.CrudRepository;

public interface ProductCacheRepository extends CrudRepository<ProductCache, String> {
}
