package com.tms.profilemanager.cache.config;

import com.tms.profilemanager.cache.RLocalCachedMapCache;
import com.tms.profilemanager.cache.config.properties.CacheManagerProperties;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

@Configuration("CustomCacheAutoConfiguration")
@EnableConfigurationProperties(CacheManagerProperties.class)
@EnableCaching
public class CacheAutoConfiguration {

    @Bean
    public CacheManager cacheManager(CacheManagerProperties cacheManagerProperties,
                                     RedissonClient redissonClient,
                                     @Autowired(required = false) ConversionService conversionService,
                                     @Value("${spring.application.name}") String applicationName) {
        List<Cache> caches = new ArrayList<>();

        cacheManagerProperties.getCaches().forEach((cacheName, cacheProperties) -> {
            if (cacheProperties.getEnabled()) {
                caches.add(new RLocalCachedMapCache(applicationName, cacheName, cacheProperties, redissonClient, conversionService));
            } else {
                caches.add(new NoOpCache(cacheName)); // disable cache
            }
        });

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return manager;
    }
}
