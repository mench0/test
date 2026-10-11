package com.tms.profilemanager.app.integration.skapps.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.integration.config.IntegrationProperties;
import com.tms.profilemanager.app.integration.skapps.dto.SkappsErrorCodeResponseDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantPatchRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantSearchResponseDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalPatchRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalSearchResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SKAppsClient {

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";


    WebClient skappsWebClient;
    SKAppsAuthClient authClient;
    IntegrationProperties integrationProperties;
    ObjectMapper objectMapper;

    /**
     * StatusIds list
     * 1=>Created
     * 2=>Pending
     * 3=>Inactive
     * 4=>Active
     */
    public MerchantSearchResponseDto searchMerchantByInnOrPinfl(String innOrPinfl) {
        return skappsGetRequest(
                integrationProperties.getSkapps().getMerchantPath(),
                Map.of("identityNumberEq", innOrPinfl, "statusIds", List.of(1, 2, 4)),
                MerchantSearchResponseDto.class
        );
    }

    public TerminalSearchResponseDto searchTerminalBySerialNumber(String serialNumber) {
        return skappsGetRequest(
                integrationProperties.getSkapps().getTerminalPath(),
                Map.of("serialNumber", serialNumber, "statusIds", List.of(1, 2, 4)),
                TerminalSearchResponseDto.class
        );
    }

    public Object createMerchant(MerchantCreateRequestDto request) {
        return skappsRequest(
                HttpMethod.POST,
                integrationProperties.getSkapps().getMerchantPath(),
                request
        );
    }

    public Object updateMerchant(String merchantId, MerchantPatchRequestDto request) {
        return skappsRequest(
                HttpMethod.PATCH,
                integrationProperties.getSkapps().getMerchantPath() + "/" + merchantId,
                request
        );
    }

    public Object createTerminal(TerminalCreateRequestDto request) {
        return  skappsRequest(
                HttpMethod.POST,
                integrationProperties.getSkapps().getTerminalPath(),
                request
        );
    }

    public Object updateTerminal(Long terminalId, TerminalPatchRequestDto request) {
        return skappsRequest(
                HttpMethod.PATCH,
                integrationProperties.getSkapps().getTerminalPath() + "/" + terminalId,
                request
        );
    }

    private <Req> Object skappsRequest(
            HttpMethod method,
            String path,
            Req requestBody
    ) {
        String token = authClient.getToken();
        IntegrationProperties.SKApps props = integrationProperties.getSkapps();

        ClientResponse responseBody = skappsWebClient.method(method)
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(props.getHost())
                        .path(path)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .bodyValue(requestBody)
                .exchange()
                .block();

        try {
            String body = responseBody.bodyToMono(String.class).block();
            return objectMapper.readValue(body, SkappsErrorCodeResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse SKApps error code: " + responseBody, e);
        }

    }

    private <Resp> Resp skappsGetRequest(
            String path,
            Map<String, ?> queryParams,
            Class<Resp> responseClass
    ) {
        String token = authClient.getToken();
        IntegrationProperties.SKApps props = integrationProperties.getSkapps();

        return skappsWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder = uriBuilder
                            .scheme("https")
                            .host(props.getHost())
                            .path(path);

                    for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Iterable<?> iterable) {
                            for (Object v : iterable) {
                                uriBuilder = uriBuilder.queryParam(entry.getKey(), v);
                            }
                        } else {
                            uriBuilder = uriBuilder.queryParam(entry.getKey(), value);
                        }
                    }

                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
    }

}
