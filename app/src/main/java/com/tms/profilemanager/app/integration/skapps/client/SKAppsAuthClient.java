package com.tms.profilemanager.app.integration.skapps.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.integration.config.IntegrationProperties;
import com.tms.profilemanager.app.integration.skapps.dto.auth.AuthRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.auth.AuthResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SKAppsAuthClient {

    private static final String SKAPPS_TOKEN = "skapps-token";

    WebClient rklWebClient;
    IntegrationProperties integrationProperties;
    ObjectMapper objectMapper;
    CacheManager cacheManager;

    @Cacheable(cacheNames = SKAPPS_TOKEN, unless = "#result == null")
    public String getToken() {
        String token = getSKAppsToken();

        if (token == null || isExpired(token)) {
            log.warn("SKApps token expired, clearing cache");
            clearCache();
            token = getSKAppsToken();
        }

        return token;
    }

    public void clearCache() {
        requireNonNull(cacheManager.getCache(SKAPPS_TOKEN)).clear();
    }

    public String getSKAppsToken() {
        String token = null;
        IntegrationProperties.SKApps propertiesSkapps = integrationProperties.getSkapps();
        try {
            AuthResponseDto responseDto = getTokenFromSKApps();
            if (responseDto != null && responseDto.getData() != null) {
                token = responseDto.getData().getToken();
            }
            return token;

        } catch (Exception e) {
            log.error("SKApps request failed. User = {}, password = {}",
                    propertiesSkapps.getUser(),
                    propertiesSkapps.getPassword(),
                    e
            );
        }
        return null;
    }

    private AuthResponseDto getTokenFromSKApps() {
        IntegrationProperties.SKApps propertiesSkapps = integrationProperties.getSkapps();
        return rklWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(propertiesSkapps.getHost())
                        .path(propertiesSkapps.getAuthPath())
                        .build())
                .contentType(APPLICATION_JSON)
                .bodyValue(AuthRequestDto.builder()
                        .username(propertiesSkapps.getUser())
                        .password(propertiesSkapps.getPassword())
                        .build())
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .block();
    }

    private boolean isExpired(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                return true;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payload = objectMapper.readTree(payloadJson);

            long exp = payload.get("exp").asLong();
            long now = System.currentTimeMillis() / 1000;

            return now >= (exp - 10);
        } catch (Exception e) {
            return true;
        }
    }
}

