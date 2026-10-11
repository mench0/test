package com.tms.profilemanager.app.integration.rkl;

import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.exception.CustomValidationException;
import com.tms.profilemanager.app.exception.EntityNotFoundByIdException;
import com.tms.profilemanager.app.integration.config.IntegrationProperties;
import com.tms.profilemanager.app.integration.rkl.dto.RKLRequestDto;
import com.tms.profilemanager.app.integration.rkl.dto.RKLRequestDto.RKLAcquirerDto;
import com.tms.profilemanager.app.integration.rkl.dto.RKLResponseDto;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestV2Dto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RKLClient {

    private static final String ACQUIRER_NOT_LINKED_WITH_TERMINAL_ERROR_CODE = "validation.terminal.not-linked-with-acquirer";

    WebClient rklWebClient;
    AcquirerService acquirerService;
    IntegrationProperties integrationProperties;

    public RKLClient(WebClient rklWebClient,
                     AcquirerService acquirerService,
                     IntegrationProperties integrationProperties) {
        this.rklWebClient = rklWebClient;
        this.acquirerService = acquirerService;
        this.integrationProperties = integrationProperties;
    }

    public RKLResponseDto getRKLResponse(Terminal terminal, TerminalEncryptedKeysRequestDto request) {
        return getRKLResponseUniversal(
                request.getAcquirersIds(),
                id -> id,
                (id, acquirer) -> buildRKLRequestDto(terminal, acquirer, request)
        );
    }

    public RKLResponseDto getRKLResponseV2(Terminal terminal, TerminalEncryptedKeysRequestV2Dto request) {
        Map<Long, Long> acquirerMap = terminal.getTerminalAcquirerParams().stream()
                .collect(Collectors.toMap(TerminalAcquirerParams::getId, TerminalAcquirerParams::getAcquirerId));
        return getRKLResponseUniversal(
                request.getAcquirers(),
                acquirerKey -> ofNullable(acquirerMap.get(acquirerKey.getAcquirerId()))
                        .orElseThrow(() -> new EntityNotFoundByIdException(Acquirer.class, acquirerKey.getAcquirerId())),
                (acquirerKey, acquirer) ->
                        buildRKLRequestDtoV2(terminal, acquirer, request, acquirerKey)
        );
    }

    private <T> RKLResponseDto getRKLResponseUniversal(
            Collection<T> items,
            Function<T, Long> acquirerIdExtractor,
            BiFunction<T, Acquirer, Object> requestBodyBuilder
    ) {
        Set<RKLResponseDto.RKLEncryptedKeyDto> allKeys = new HashSet<>();

        for (T item : items) {
            Long acquirerId = acquirerIdExtractor.apply(item);
            Acquirer acquirer = acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId)
                    .orElseThrow(() -> new EntityNotFoundByIdException(Acquirer.class, acquirerId));
            Acquirer.RklUrl rklHost = acquirer.getRklUrl();
            String url = (rklHost != null && TRUE.equals(rklHost.getRklEnable()))
                    ? buildUrl(rklHost)
                    : integrationProperties.getRkl().getDefaultRequestUrl();

            try {
                RKLResponseDto response = executeRklRequest(url, requestBodyBuilder.apply(item, acquirer));
                if (response != null && response.getEncryptedKeys() != null) {
                    allKeys.addAll(response.getEncryptedKeys());
                }
            } catch (Exception e) {
                log.error("RKL request failed for acquirer {}", acquirer.getName(), e);
            }
        }

        return RKLResponseDto.builder()
                .encryptedKeys(allKeys)
                .build();
    }

    private <T> RKLResponseDto executeRklRequest(String url, T requestBody) {
         return rklWebClient.post()
                    .uri(url)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(RKLResponseDto.class)
                    .block();
    }

    private String buildUrl(Acquirer.RklUrl rklUrl) {
        boolean useSsl = Boolean.TRUE.equals(rklUrl.getRklUseSsl());
        return UriComponentsBuilder.newInstance()
                .scheme(useSsl ? "https" : "http")
                .host(rklUrl.getRklHost())
                .port(rklUrl.getRklPort() != null ? rklUrl.getRklPort() : (useSsl ? 443 : 80))
                .path(rklUrl.getRklPath())
                .build()
                .toString();
    }

    private RKLRequestDto buildRKLRequestDto(Terminal terminal, Acquirer acquirer, TerminalEncryptedKeysRequestDto request) {
        return RKLRequestDto.builder()
                .serialNumber(terminal.getSerialNumber())
                .algorithm(request.getAlgorithm())
                .cypherKey(request.getCypherKey())
                .acquirers(Set.of(buildRKLAcquirerDto(terminal, acquirer)))
                .groupName(Optional.ofNullable(terminal.getGroup()).map(TerminalGroup::getName).orElse(null))
                .build();
    }

    private RKLAcquirerDto buildRKLAcquirerDto(Terminal terminal, Acquirer acquirer) {
        var params = terminal.getTerminalAcquirerParams().stream()
                .filter(it -> it.getAcquirerId().equals(acquirer.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomValidationException()
                        .addValidationError(TerminalEncryptedKeysRequestDto.Fields.acquirersIds, ACQUIRER_NOT_LINKED_WITH_TERMINAL_ERROR_CODE));

        return RKLAcquirerDto.builder()
                .acquirerId(acquirer.getId())
                .isoTerminalId(params.getIsoTerminalId())
                .isoMerchantId(params.getIsoMerchantId())
                .keyScheme(ofNullable(acquirer.getKeySettings()).map(Acquirer.KeySettings::getKeyScheme).orElse(null))
                .build();
    }

    private RKLRequestDto buildRKLRequestDtoV2(Terminal terminal, Acquirer acquirer,
                                               TerminalEncryptedKeysRequestV2Dto request,
                                               TerminalEncryptedKeysRequestV2Dto.AcquirerTerminalKey acquirerTerminalKey) {
        return RKLRequestDto.builder()
                .serialNumber(terminal.getSerialNumber())
                .algorithm(request.getAlgorithm())
                .cypherKey(request.getCypherKey())
                .acquirers(Set.of(buildRKLAcquirerDtoV2(terminal, acquirer, acquirerTerminalKey)))
                .groupName(Optional.ofNullable(terminal.getGroup()).map(TerminalGroup::getName).orElse(null))
                .build();
    }

    private RKLAcquirerDto buildRKLAcquirerDtoV2(Terminal terminal, Acquirer acquirer,
                                                TerminalEncryptedKeysRequestV2Dto.AcquirerTerminalKey acquirerTerminalKey) {
        boolean exists = terminal.getTerminalAcquirerParams().stream()
                .anyMatch(it -> it.getId().equals(acquirerTerminalKey.getAcquirerId()));

        if (!exists) {
            throw new CustomValidationException()
                    .addValidationError(
                            TerminalEncryptedKeysRequestDto.Fields.acquirersIds,
                            ACQUIRER_NOT_LINKED_WITH_TERMINAL_ERROR_CODE
                    );
        }
        return RKLAcquirerDto.builder()
                .acquirerId(acquirer.getId())
                .isoTerminalId(acquirerTerminalKey.getTid())
                .isoMerchantId(acquirerTerminalKey.getMid())
                .keyScheme(ofNullable(acquirer.getKeySettings()).map(Acquirer.KeySettings::getKeyScheme).orElse(null))
                .build();
    }

}
