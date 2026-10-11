package com.tms.profilemanager.cache;

import com.tms.profilemanager.cache.config.properties.CacheProperties;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RLocalCachedMapCache implements Cache {

    String cacheName;
    RLocalCachedMap<String, Object> cachedMap;
    ConversionService conversionService;

    public RLocalCachedMapCache(String applicationName,
                                String cacheName,
                                CacheProperties cacheProperties,
                                RedissonClient redissonClient,
                                @Autowired(required = false) ConversionService conversionService) {
        this.cacheName = cacheName;
        this.cachedMap = redissonClient.getLocalCachedMap(
                applicationName + "::" + cacheName,
                LocalCachedMapOptions.<String, Object>defaults()
                        .evictionPolicy(cacheProperties.getEvictionPolicy())
                        .syncStrategy(cacheProperties.getSyncStrategy())
                        .reconnectionStrategy(cacheProperties.getReconnectionStrategy())
                        .timeToLive(cacheProperties.getTtl().toMillis(), TimeUnit.MILLISECONDS)
                        .cacheSize(cacheProperties.getMaxNumberOfElements())
                        .maxIdle(0)
        );
        this.cachedMap.preloadCache(); // warm up local cache
        this.conversionService = conversionService;
    }

    @Override
    @NonNull
    public String getName() {
        return cacheName;
    }

    @Override
    @NonNull
    public Object getNativeCache() {
        return cachedMap;
    }

    @Override
    public ValueWrapper get(@NonNull Object key) {
        Object value = cachedMap.get(convertKeyToString(key));
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Object key, Class<T> type) {
        Object value = cachedMap.get(convertKeyToString(key));
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        return conversionService.convert(value, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        key = convertKeyToString(key);

        Object value = cachedMap.get(key);
        if (value != null) {
            return (T) value;
        }
        T loadedValue;
        try {
            loadedValue = valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        put(key, loadedValue);
        return loadedValue;
    }

    @Override
    public void put(@NonNull Object key, Object value) {
        cachedMap.put(convertKeyToString(key), value);
    }

    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        value = cachedMap.putIfAbsent(convertKeyToString(key), value);
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    public void evict(@NonNull Object key) {
        cachedMap.remove(convertKeyToString(key));
    }

    @Override
    public void clear() {
        cachedMap.clear();
    }

    private String convertKeyToString(@NonNull Object key) {
        if (key instanceof String keyStr) {
            return keyStr;
        }
        if (conversionService != null && conversionService.canConvert(key.getClass(), String.class)) {
            return conversionService.convert(key, String.class);
        }
        return key.toString();
    }
}
