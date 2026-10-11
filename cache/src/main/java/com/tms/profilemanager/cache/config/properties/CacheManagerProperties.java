package com.tms.profilemanager.cache.config.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
@ConfigurationProperties(prefix = "app")
public class CacheManagerProperties {

    Map<@NotBlank String, @NotNull @Valid CacheProperties> caches = new HashMap<>();
}
