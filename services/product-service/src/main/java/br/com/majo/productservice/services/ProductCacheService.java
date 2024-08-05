package br.com.majo.productservice.services;

import br.com.majo.productservice.infra.cache.ProductCache;
import br.com.majo.productservice.repositories.ProductCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductCacheService {

    @Autowired
    private ProductCacheRepository productCacheRepository;

    public void saveLastVersion(ProductCache product){
        productCacheRepository.save(product); // armazenando versão antiga do produto no cache

        log.info("success saved product in cache");
    }

    public void saveLastVersion(String id, Object object){
        // armazenando versão antiga do produto no cache

        if (object instanceof Boolean){
            productCacheRepository.save(ProductCache.builder()
                    .id(id)
                    .soldOut((Boolean) object)
                    .build());
        }

        if (object instanceof String){
            productCacheRepository.save(ProductCache.builder()
                    .id(id)
                    .urlImage((String) object)
                    .build());
        }

        log.info("success saved product in cache");
    }
}
