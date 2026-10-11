package com.tms.profilemanager.cache.config.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.redisson.api.LocalCachedMapOptions.EvictionPolicy;
import org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.LocalCachedMapOptions.SyncStrategy;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CacheProperties {

    @NotNull
    Boolean enabled;

    @NotNull
    EvictionPolicy evictionPolicy = EvictionPolicy.LRU;

    @NotNull
    SyncStrategy syncStrategy = SyncStrategy.INVALIDATE;

    @NotNull
    ReconnectionStrategy reconnectionStrategy = ReconnectionStrategy.CLEAR;

    @NotNull
    Duration ttl = Duration.ZERO; // default unlimited

    @PositiveOrZero
    int maxNumberOfElements = 0; // default unlimited
}
