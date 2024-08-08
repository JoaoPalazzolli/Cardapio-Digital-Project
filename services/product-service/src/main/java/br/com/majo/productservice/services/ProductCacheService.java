package br.com.majo.productservice.services;

import br.com.majo.productservice.infra.cache.ProductCache;
import br.com.majo.productservice.repositories.ProductCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ProductCacheService {

    @Autowired
    private ProductCacheRepository productCacheRepository;

    public void saveLastVersion(ProductCache product) {
        productCacheRepository.save(product); // armazenando versão antiga do produto no cache

        log.info("success saved product in cache");
    }

    public void saveLastVersion(String id, Object object, UUID restaurantId) {
        // armazenando versão antiga do produto no cache

        var lastProduct = ProductCache.builder()
                .id(id)
                .restaurantId(restaurantId)
                .build();

        if (object instanceof Boolean) lastProduct.setSoldOut((Boolean) object);

        if (object instanceof String) lastProduct.setUrlImage((String) object);

        productCacheRepository.save(lastProduct);

        log.info("success saved product in cache");
    }
}
